package databases;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.text.format.DateFormat;
import android.util.Log;
import databases.FoodItemsContract.AllFoods;
import databases.FoodItemsContract.ExpiredEntry;
import databases.FoodItemsContract.NonExpiredEntry;;

public class SQLiteHelper extends SQLiteOpenHelper
{	
	public SQLiteHelper (Context context)
	{
		super (context, "food.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		
		String CREATE_EXPIRED_TABLE  = "CREATE TABLE IF NOT EXISTS " + ExpiredEntry.TABLE_NAME + "("
				+  ExpiredEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+  ExpiredEntry.ITEM_NAME + " TEXT NOT NULL,"
				+  ExpiredEntry.EXPIRATION_DATE + " DATE NOT NULL"
				+ ");";
		
		String CREATE_CURRENT_TABLE = "CREATE TABLE IF NOT EXISTS " + NonExpiredEntry.TABLE_NAME + "("
				+  NonExpiredEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+  NonExpiredEntry.ITEM_NAME + " TEXT NOT NULL,"
				+  NonExpiredEntry.EXPIRATION_DATE + " DATE NOT NULL"
				+ ");";
		
		String CREATE_ALL_TABLE = "CREATE TABLE IF NOT EXISTS " + AllFoods.TABLE_NAME + "("
				+  AllFoods._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+  AllFoods.ITEM_NAME + " TEXT NOT NULL"
				+ ");";
		db.execSQL(CREATE_CURRENT_TABLE);
		db.execSQL(CREATE_EXPIRED_TABLE);
		db.execSQL(CREATE_ALL_TABLE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		// Note that this only fires if you change the version number for your database.
		// It does NOT depend on the version number for your application.
		// If you want to update the schema without wiping data, commenting out the next 2 lines
		// should be your top priority before modifying this method.
		db.execSQL("DROP TABLE IF EXISTS " + ExpiredEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + NonExpiredEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AllFoods.TABLE_NAME);
		onCreate(db);
	}

	public void insertNonExpiredFood (String itemName, String date)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();	
		values.put(NonExpiredEntry.ITEM_NAME,itemName);
		values.put(NonExpiredEntry.EXPIRATION_DATE, date);		
		db.insert(NonExpiredEntry.TABLE_NAME, null, values);
		db.close();
		
	}
	
	public void insertExpiredFood (String itemName, String date)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();	
		values.put(ExpiredEntry.ITEM_NAME,itemName);
		values.put(ExpiredEntry.EXPIRATION_DATE, date);		
		db.insert(ExpiredEntry.TABLE_NAME, null, values);
		db.close();
	}	
	
	private void insertAllFood(SQLiteDatabase db,String itemName){		
		ContentValues values = new ContentValues();	
		values.put(AllFoods.ITEM_NAME,itemName);		
	}
	
	public ArrayList<FoodItem> getAllNonExpiredFood(){
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<FoodItem> entries =  new ArrayList<FoodItem>();
		
		 String[] columns = {
	                NonExpiredEntry._ID,
	                NonExpiredEntry.ITEM_NAME,
	                NonExpiredEntry.EXPIRATION_DATE
	        };

		 Cursor cursor = db.query(
	                NonExpiredEntry.TABLE_NAME,  // Table to Query
	                columns,
	                null, // Columns for the "where" clause
	                null, // Values for the "where" clause
	                null, // columns to group by
	                null, // columns to filter by row groups
	                null // sort order
	        );
		while(cursor.moveToNext()){
			int index = cursor.getColumnIndex(NonExpiredEntry.ITEM_NAME);
            String name = cursor.getString(index);
            
            index = cursor.getColumnIndex(NonExpiredEntry.EXPIRATION_DATE);
            String date = cursor.getString(index); 
            
            index = cursor.getColumnIndex(NonExpiredEntry._ID);
            int id = cursor.getInt(index); 
            
            entries.add(new FoodItem(name, date, null,id));      
		}		
		db.close();
		return entries;
	}
	
	public ArrayList<FoodItem> getAllExpiredFood(){
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<FoodItem> entries =  new ArrayList<FoodItem>();
		
		 String[] columns = {
	                ExpiredEntry._ID,
	                ExpiredEntry.ITEM_NAME,
	                ExpiredEntry.EXPIRATION_DATE
	        };

		 Cursor cursor = db.query(
	                ExpiredEntry.TABLE_NAME,  // Table to Query
	                columns,
	                null, // Columns for the "where" clause
	                null, // Values for the "where" clause
	                null, // columns to group by
	                null, // columns to filter by row groups
	                null // sort order
	        );
		while(cursor.moveToNext()){
			int index = cursor.getColumnIndex(ExpiredEntry.ITEM_NAME);
            String name = cursor.getString(index);
            
            index = cursor.getColumnIndex(ExpiredEntry.EXPIRATION_DATE);
            String date = cursor.getString(index); 
            
            index = cursor.getColumnIndex(ExpiredEntry._ID);
            int id = cursor.getInt(index); 
            
            entries.add(new FoodItem(name, date, null,id));      
		}		
		db.close();
		return entries;
	}
	
	public int moveExpiredFood(){
		SQLiteDatabase db = this.getWritableDatabase();	
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = df.format(c.getTime());
		String query = "SELECT * " + "FROM " + NonExpiredEntry.TABLE_NAME;
		int count = 0;
		Cursor cursor = db.rawQuery(query,null);	
		 Log.d("SQ_LITE_DEBUG","date is " + formattedDate);
		
		while(cursor.moveToNext()){
			int index = cursor.getColumnIndex(NonExpiredEntry.ITEM_NAME);
            String name = cursor.getString(index);            
            index = cursor.getColumnIndex(NonExpiredEntry.EXPIRATION_DATE);
            String date = cursor.getString(index); 
            
            index = cursor.getColumnIndex(NonExpiredEntry._ID);
            int id = cursor.getInt(index);
            
            if(date.compareTo(formattedDate) <= 0){
            	//delete for non expired
            	db.delete(NonExpiredEntry.TABLE_NAME, NonExpiredEntry._ID + "=" + id, null);            
            	ContentValues values = new ContentValues();	          
    			values.put(ExpiredEntry.ITEM_NAME,name);
    			values.put(ExpiredEntry.EXPIRATION_DATE, date);		
    			db.insert(ExpiredEntry.TABLE_NAME, null, values);
    			count++;
            }
		}		
		
		db.close();
		return count;
	}	
	
	public void removeNonExpiredFood (int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();			
		db.delete(NonExpiredEntry.TABLE_NAME, NonExpiredEntry._ID + "=" + id, null);
		db.close();
	}	
	
	public void removeExpiredFood (int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();			
		db.delete(ExpiredEntry.TABLE_NAME, ExpiredEntry._ID + "=" + id, null);
		db.close();
	}
	
	public ArrayList<String> getNonExpiredNames(int maxDays){
		SQLiteDatabase db = this.getWritableDatabase();					
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
		ArrayList<String> validItems = new ArrayList<String>();		
		
		Date currDate = new Date();
		long curr_date = currDate.getTime();
		String query = "SELECT * " + "FROM " + NonExpiredEntry.TABLE_NAME;
		Cursor cursor = db.rawQuery(query,null);
		
		while(cursor.moveToNext()){
			int index = cursor.getColumnIndex(NonExpiredEntry.ITEM_NAME);
            String name = cursor.getString(index);            
            index = cursor.getColumnIndex(NonExpiredEntry.EXPIRATION_DATE);
            String date = cursor.getString(index);           
            try {
				Date result =  df.parse(date);
				long exp_date = result.getTime();
				long diff = exp_date-curr_date;
				int num_days = (int) (diff/(1000*60*60*24));
				Log.d("DIFFERENCE IN DATES","Item is " + name + " difference is " + num_days);
				if(num_days <= maxDays){
					validItems.add(name);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}          
		}	
		db.close();
		return validItems;		
	}
	
	
}
