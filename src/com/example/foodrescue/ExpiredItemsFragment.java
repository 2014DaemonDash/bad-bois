package com.example.foodrescue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpiredItemsFragment extends Fragment {
	
	public ExpiredItemsFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.expired_items_fragment,container, false);
		
		return rootView;
	}
	
}
