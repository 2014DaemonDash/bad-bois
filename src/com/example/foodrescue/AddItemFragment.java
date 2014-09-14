package com.example.foodrescue;

import java.sql.Date;
import java.text.SimpleDateFormat;

import java.util.Locale;

import databases.SQLiteHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

public class AddItemFragment extends Fragment implements OnClickListener {

	private Button takePhotoButton;
	private Button saveItemButton;
	private ImageView foodImage;
	private EditText foodNameET;
	private DatePicker datePicker;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private static String LOG_TAG = AddItemFragment.class.getCanonicalName();
	private SQLiteHelper database;

	
	public AddItemFragment(){		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG,"Activity Created");
		super.onCreate(savedInstanceState);
		database = new SQLiteHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.add_item_fragment,container, false);
		initializeUI(rootView);		
		return rootView;
	}
	
	private void initializeUI(View v){
		takePhotoButton = (Button) v.findViewById(R.id.bTakePhoto);
		takePhotoButton.setOnClickListener(this);
		foodImage = (ImageView)v.findViewById(R.id.ivFoodImage);
		saveItemButton = (Button) v.findViewById(R.id.bSave);
		saveItemButton.setOnClickListener(this);
		foodNameET = (EditText)v.findViewById(R.id.etNameField);
		datePicker = (DatePicker)v.findViewById(R.id.dpExpirateDate);
		
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bTakePhoto:
			//start the camera app
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!= null){
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
			break;
		case R.id.bSave:
			String itemName = foodNameET.getText().toString();
			int year = datePicker.getYear();
			int month = datePicker.getMonth();
			int day = datePicker.getDayOfMonth();
			String date= getDateTime(year, month, day);
			database.insertNonExpiredFood(itemName,date);
			break;
		}
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_IMAGE_CAPTURE){
			Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        foodImage.setImageBitmap(imageBitmap);	        
		}
	}
	
	private String getDateTime(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Log.d("DATE-YEAR", "YEAR IS " + year);
        Date date = new Date(year-1900,month,day);       
        return dateFormat.format(date);
}
	
	
	
	
	

}
