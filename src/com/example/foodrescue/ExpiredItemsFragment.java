package com.example.foodrescue;

import java.util.ArrayList;

import databases.FoodItem;
import databases.SQLiteHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ExpiredItemsFragment extends Fragment {
	
	private ArrayList<FoodItem> foodItems;
	private SQLiteHelper db;
	private static final String[] editNames={"Delete", "Edit"};
	private ListView list;
	private ArrayAdapter<FoodItem> adapter;
	
	public ExpiredItemsFragment(){}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db = new SQLiteHelper(getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.expired_items_fragment,container, false);
		foodItems = db.getAllExpiredFood();
		populateListView(rootView);
		return rootView;
	}
	
	private void populateListView(View v) {		
		adapter = new MyListAdapter();		
		list = (ListView)v.findViewById(R.id.listview_expired_items);
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
			                	   db.removeExpiredFood(foodItems.get(position).getId());
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
			foodItems = db.getAllExpiredFood();
			adapter = new MyListAdapter();
			list.setAdapter(adapter);
	    }
	}
	
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
			
			
			//Setting the name
			TextView nameText = (TextView)itemView.findViewById(R.id.tvListFoodItem);
			TextView dateText = (TextView)itemView.findViewById(R.id.tvLstExpDate);
			nameText.setText(currentItem.getFoodName());
			dateText.setText(currentItem.getExpirationDate());
			
			return itemView;			
		}		
	}
	
}
