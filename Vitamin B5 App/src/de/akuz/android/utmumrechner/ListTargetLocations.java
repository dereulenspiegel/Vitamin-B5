package de.akuz.android.utmumrechner;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import de.akuz.android.utmumrechner.views.TargetLocationView;

public class ListTargetLocations extends MyAbstractActivity {
	
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
	}

}
