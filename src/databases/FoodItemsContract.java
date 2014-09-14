package databases;

import android.provider.BaseColumns;

public class FoodItemsContract {
	public static final class NonExpiredEntry implements BaseColumns {

        public static final String TABLE_NAME = "non_expired_food";        
        public static final String COLUMN_FOOD_KEY = "non_expired_id";
        public static final String EXPIRATION_DATE = "expiration_date";
        public static final String ITEM_NAME = "item_name";
        
        
    }

    public static final class ExpiredEntry implements BaseColumns{
        
    	public static final String TABLE_NAME = "expired_food";        
        public static final String COLUMN_FOOD_KEY = "expired_id";
        public static final String EXPIRATION_DATE = "expiration_date";
        public static final String ITEM_NAME = "item_name";
    }
    
public static final class AllFoods implements BaseColumns{
	
    	public static final String TABLE_NAME = "all_food";  
    	public static final String COLUMN_FOOD_KEY = "food_id";
    	public static final String ITEM_NAME = "item_name";  
       
    }	
}
