package de.akuz.android.utmumrechner.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MyAbstractFragment extends Fragment {
	
	private int layoutId;
	
	protected View layout;
	
	protected void setContentView(int id){
		layoutId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(layoutId, container, false);
		initUIElements();
		return layout;
	}
	
	public View findViewById(int id){
		return layout.findViewById(id);
	}
	
	protected abstract void initUIElements();

}
