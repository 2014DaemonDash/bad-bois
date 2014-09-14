package com.example.foodrescue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import databases.SQLiteHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
	private Button scanButton;
	private ImageView foodImage;
	private EditText foodNameET;
	private DatePicker datePicker;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_BARCODE_SCAN = 0;
	private static String LOG_TAG = AddItemFragment.class.getCanonicalName();
	private SQLiteHelper database;

	public AddItemFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Activity Created");
		super.onCreate(savedInstanceState);
		database = new SQLiteHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.add_item_fragment, container,
				false);
		initializeUI(rootView);
		return rootView;
	}

	private void initializeUI(View v) {
		takePhotoButton = (Button) v.findViewById(R.id.bTakePhoto);
		takePhotoButton.setOnClickListener(this);
		foodImage = (ImageView) v.findViewById(R.id.ivFoodImage);

		saveItemButton = (Button) v.findViewById(R.id.bSave);
		saveItemButton.setOnClickListener(this);

		scanButton = (Button) v.findViewById(R.id.bScanCode);
		scanButton.setOnClickListener(this);
		foodNameET = (EditText) v.findViewById(R.id.etNameField);
		datePicker = (DatePicker) v.findViewById(R.id.dpExpirateDate);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bTakePhoto:
			// start the camera app
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getActivity()
					.getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
			break;
		case R.id.bSave:
			String itemName = foodNameET.getText().toString();
			int year = datePicker.getYear();
			int month = datePicker.getMonth();
			int day = datePicker.getDayOfMonth();
			String date = getDateTime(year, month, day);
			database.insertNonExpiredFood(itemName, date);			
			break;
		case R.id.bScanCode:
			
			startBarCodeScan();
			break;
		}

	}

	private void startBarCodeScan() {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.setPackage("com.google.zxing.client.android");
		intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE",
				"QR_CODE_MODE");
		startActivityForResult(intent, REQUEST_BARCODE_SCAN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case REQUEST_IMAGE_CAPTURE:
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data");
				foodImage.setImageBitmap(imageBitmap);
				break;
			case REQUEST_BARCODE_SCAN:
				String contents = data.getStringExtra("SCAN_RESULT");
				Log.d("BAR_CODE_SCAN", contents);				
				FetchInfoTask infoTask = new FetchInfoTask();
				infoTask.execute(contents);
				break;

			}
		}
	}

	private String getDateTime(int year, int month, int day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Log.d("DATE-YEAR", "YEAR IS " + year);
		Date date = new Date(year - 1900, month, day);
		return dateFormat.format(date);
	}

	public class FetchInfoTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... contents) {
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			try {
				Log.d("FetchInfoTask", contents[0]);
				final String BARCODE_BASE_URL = "http://api.upcdatabase.org/json/b46e5640a42c5ef30d2435ab7b8e5aae/"
						+ contents[0];
				Log.d("FetchInfoTask", BARCODE_BASE_URL);
				Uri builtUri = Uri.parse(BARCODE_BASE_URL);
				URL url = new URL(builtUri.toString());

				// Create the request to upcdatabase, and open the connection
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				// Read the input stream to a String
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
			Log.d("FetchInfoTask-JSON Response", info);
			try {
				JSONObject upcJson = new JSONObject(info);				
				if (Boolean.valueOf(upcJson.getString("valid"))) {
					String itemname = upcJson.getString("itemname");
					if(!(itemname.length() > 0)){
						itemname = upcJson.getString("description");
					}
					foodNameET.setText(itemname);
					// itemPrice.setText(upcJson.getString("avg_price"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
