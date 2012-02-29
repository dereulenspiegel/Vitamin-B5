package de.akuz.android.utmumrechner;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import de.akuz.android.utmumrechner.views.TargetLocationView;

public class ListTargetLocations extends MyAbstractActivity implements OnClickListener {
	
	private static class LocationListAdapter extends ArrayAdapter<TargetLocation>{

		public LocationListAdapter(Context context, int resource,
				int textViewResourceId, List<TargetLocation> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TargetLocation location = getItem(position);
			if(convertView != null){
				((TargetLocationView)convertView).bindTargetLocation(location);
				return convertView;
			} else {
				TargetLocationView view = new TargetLocationView(getContext());
				view.bindTargetLocation(location);
				return view;
			}
		}
		
	}
	
	private LocationDatabase db;
	
	private Button buttonAddLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		db = new LocationDatabase(this);
		db.open();
		setContentView(R.layout.location_list);
		LocationListAdapter adapter = new LocationListAdapter(this, 0, 0, db.getAllLocations());
		db.close();
		ListView listView = (ListView)findViewById(R.id.listViewLoactions);
		listView.setAdapter(adapter);
		initUIElements();
	}
	
	private void initUIElements(){
		buttonAddLocation = (Button)findViewById(R.id.buttonAddLocation);
		buttonAddLocation.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == buttonAddLocation.getId()){
			Intent i = new Intent(this,AddLocationActivity.class);
			startActivity(i);
		}
		
	}

}
