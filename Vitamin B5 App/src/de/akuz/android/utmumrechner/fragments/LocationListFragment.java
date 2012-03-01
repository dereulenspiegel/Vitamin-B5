package de.akuz.android.utmumrechner.fragments;

import java.util.ArrayList;
import java.util.List;

import de.akuz.android.utmumrechner.AddLocationActivity;
import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.views.TargetLocationView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationListFragment extends MyAbstractFragment implements OnClickListener, OnItemClickListener{
	
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
				view.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
				return view;
			}
		}
		
	}
	
	public interface Callback{
		public void itemSelected(long id);
		public void itemDeleted(long id);
	}
	
	private LocationDatabase db;
	
	private Button buttonAddLocation;
	private Button buttonDelete;
	private ListView listView;
	private ListAdapter adapter;
	
	private List<Callback> callbacks;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		callbacks = new ArrayList<Callback>();
		db = new LocationDatabase(this.getActivity());
		setContentView(R.layout.fragment_location_list);
	}
	
	@Override
	protected void initUIElements(){
		buttonAddLocation = (Button)findViewById(R.id.buttonAddLocation);
		buttonAddLocation.setOnClickListener(this);
		buttonDelete = (Button)findViewById(R.id.buttonDelete);
		buttonDelete.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listViewLoactions);
		db.open();
		adapter = new LocationListAdapter(this.getActivity(), 0, 0, db.getAllLocations());
		db.close();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == buttonAddLocation.getId()){
			Intent i = new Intent(this.getActivity(),AddLocationActivity.class);
			startActivity(i);
		} else if(v.getId() == buttonDelete.getId()){
			deleteSelectedLocations();
		}
		
	}
	
	public void addCallback(Callback callback){
		if(callback != null){
			callbacks.add(callback);
		}
	}
	
	public void removeCallback(Callback callback){
		if(callback != null){
			callbacks.remove(callback);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long listId) {
		long id = ((TargetLocation)adapter.getItem(position)).getId();
		for(Callback c : callbacks){
			c.itemSelected(id);
		}
	}
	
	public ListAdapter getListAdapter(){
		return adapter;
	}
	
	private void deleteSelectedLocations(){
		List<TargetLocation> locations = new ArrayList<TargetLocation>(adapter.getCount());
		for(int i=0; i<adapter.getCount();i++){
			TargetLocation tempLocation = (TargetLocation) adapter.getItem(i);
			if(tempLocation.isSelected()){
				locations.add(tempLocation);
			}
		}
		db.open();
		for(TargetLocation t : locations){
			db.deleteLocation(t);
			for(Callback c : callbacks){
				c.itemDeleted(t.getId());
			}
		}
		adapter = new LocationListAdapter(this.getActivity(),-1, -1, db.getAllLocations());
		db.close();
		listView.setAdapter(adapter);
	}

}
