package com.example.foodrescue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import databases.SQLiteHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListRecipes extends Activity {
	
	private SharedPreferences sharedPreferences;
	private SQLiteHelper sql;
	private ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> foodItems;
	private ArrayList<String> foodIds;
    ListView listView;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipes_list_layout);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sql = new SQLiteHelper(getApplicationContext());
		arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.restaurant_food_textview,
                R.id.tvRestaurantFoodItem,
                new ArrayList<String>());
		foodItems = new ArrayList<String>();
		foodIds = new ArrayList<String>();
       listView = (ListView)findViewById(R.id.listview_recipe_items);
       listView.setAdapter(arrayAdapter);
       fillList();
		
	}
	
	private void fillList(){
		String numStr = sharedPreferences.getString(getString(R.string.pref_recipe_key),getString(R.string.pref_recipe_default));
		int maxDays = Integer.parseInt(numStr);
		ArrayList<String> result = sql.getNonExpiredNames(maxDays);
		FetchRecipeTask frt = new FetchRecipeTask();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String foodId = foodIds.get(position);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.yummly.com/recipe/" + foodId));
				startActivity(browserIntent);
				
			}
        });
		frt.execute(result);
	}

	public class FetchRecipeTask extends AsyncTask<ArrayList<String>,Void,String> {
		protected String doInBackground(ArrayList<String>... contents) {
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			try{
				
				String foodNames = "";
				for(String s : contents[0]){
					foodNames += s + "+";
				}
				foodNames=foodNames.substring(0, foodNames.length()-1);
				final String RECIPE_BASE_URL = "http://api.yummly.com/v1/api/recipes?_app_id=f3ced0f7&_app_key=7a2c23edfb2bac637a84a05082a8b5c2&q=" 
							+ foodNames;
				Log.d("FetchRecipeTask",RECIPE_BASE_URL);
				Uri builtUri = Uri.parse(RECIPE_BASE_URL);
				URL url = new URL(builtUri.toString());

				//Create the request to upcdatabase, and open the connection
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				//Read the input stream to a String
				InputStream inputStream = urlConnection.getInputStream();
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					return null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				if (buffer.length() == 0) {
					return null;
				}
				return buffer.toString();

			} catch (IOException e) {
				return null;
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {

					}
				}
			}
		}
		
		protected void onPostExecute(String info) {
            Log.d("FetchInfoTask", info);
            try {
				JSONObject recipeJson = new JSONObject(info);
				if (recipeJson.getInt("totalMatchCount") > 0) {
					JSONArray arr = recipeJson.getJSONArray("matches");
					for (int i=0; i<Math.min(arr.length(), 5); i++) {
						JSONObject recipe = arr.getJSONObject(i);
						String id = recipe.getString("id");
						String name = recipe.getString("recipeName");
						int rating = recipe.getInt("rating");
						Log.d("Fetch", id);
						Log.d("Fetch", name);
						foodItems.add(name);
						foodIds.add(id);
						
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
            arrayAdapter.clear();
            arrayAdapter.addAll(foodItems);
        }
	}
	
	

}
