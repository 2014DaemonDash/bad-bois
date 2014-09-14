package com.example.foodrescue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



import databases.FoodItem;
import databases.SQLiteHelper;
import databases.FoodItemsContract.AllFoods;
import databases.FoodItemsContract.ExpiredEntry;
import databases.FoodItemsContract.NonExpiredEntry;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.ListPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NonExpiredFragment extends Fragment implements android.view.View.OnClickListener {
	
public  NonExpiredFragment() {}

	private ArrayAdapter<FoodItem> forecastAdapter;
	private ArrayList<FoodItem> foodItems;
	private ListView list;
	private ArrayAdapter<FoodItem> adapter;
	private Button getRecipeButton;
	private SharedPreferences sharedPreferences;
	private SQLiteHelper sql;
	private static final String LOG_TAG = NonExpiredFragment.class.getCanonicalName();
	
	private static final String[] editNames={"Delete", "Edit"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		sql = new SQLiteHelper(getActivity());		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.valid_items_fragment,container, false);
		getRecipeButton = (Button)rootView.findViewById(R.id.bGetRecipes);
		getRecipeButton.setOnClickListener(this);
		foodItems = sql.getAllNonExpiredFood();		
		populateListView(rootView);		
		return rootView;
	}
	
	private void populateListView(View v) {
		Log.d(LOG_TAG,"In populate view");
		adapter = new MyListAdapter();
		
		list = (ListView)v.findViewById(R.id.listview_non_expired_items);
		list.setAdapter(adapter);	
		list.setLongClickable(true);
		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {			        
			        
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setTitle("Pick Option")
			               .setItems(R.array.edit_options_array, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int which) {
			                   switch(which){	
			                   case 0:
			                	   sql.removeNonExpiredFood(foodItems.get(position).getId());
			                	   foodItems.remove(position);			                	   
			                	   list.setAdapter(adapter);
			                	   break;
			                   case 1:
			                   }
			               }
			        }).show();		        
			        return false;
			}
		

	});
	}

	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (this.isVisible()) {	
			Log.d("VISIBILITY", "VISIBLE!!!!!!!");			
			foodItems = sql.getAllNonExpiredFood();
			adapter = new MyListAdapter();
			list.setAdapter(adapter);
	    }
	}
	//inner class for list adapter
		private class MyListAdapter extends ArrayAdapter<FoodItem>{
			public MyListAdapter(){
				super(getActivity(),R.layout.non_expired_listitem,foodItems);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				
				// Make sure we have a view to work with
				View itemView = convertView;
				if(itemView == null){
					itemView = getActivity().getLayoutInflater().inflate(R.layout.non_expired_listitem, parent,false);
				}
			
				FoodItem currentItem = foodItems.get(position);			
				
				Log.d(LOG_TAG,"Editing fields in Adapter");
				//Setting the name
				TextView nameText = (TextView)itemView.findViewById(R.id.tvListFoodItem);
				TextView dateText = (TextView)itemView.findViewById(R.id.tvLstExpDate);
				nameText.setText(currentItem.getFoodName());
				dateText.setText(currentItem.getExpirationDate());
				
				return itemView;			
			}		
		}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bGetRecipes:
			Log.d("RECIPES_DEBUG", "in recipes button");			
			String numStr = sharedPreferences.getString(getString(R.string.pref_recipe_key),getString(R.string.pref_recipe_default));
			int maxDays = Integer.parseInt(numStr);
			Log.d("SETTINGS","Days preference is " + maxDays);
			
			Intent intent = new Intent(getActivity(),ListRecipes.class);
			startActivity(intent);
			ArrayList<String> result = sql.getNonExpiredNames(maxDays);			
			break;
		}
		
	}
	
		
		
		

}
