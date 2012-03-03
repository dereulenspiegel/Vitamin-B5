package de.akuz.android.utmumrechner.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.ActionProvider.SubUiVisibilityListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.LocationExporter;
import de.akuz.android.utmumrechner.views.TargetLocationView;

public class LocationListFragment extends MyAbstractFragment implements
		 OnItemClickListener, LocationExporter.ExportListener {

	private static class LocationListAdapter extends
			ArrayAdapter<TargetLocation> {

		public LocationListAdapter(Context context, int resource,
				int textViewResourceId, List<TargetLocation> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TargetLocation location = getItem(position);
			if (convertView != null) {
				((TargetLocationView) convertView).bindTargetLocation(location);
				return convertView;
			} else {
				TargetLocationView view = new TargetLocationView(getContext());
				view.bindTargetLocation(location);
				view.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
				return view;
			}
		}

	}
	
	public static class ShareActionProviderCompat extends ActionProvider {
		
		private ShareActionProvider mShareActionProvider;
		
		public ShareActionProviderCompat(Context context) {
			super(context);
			mShareActionProvider = new ShareActionProvider(context);
		}

		@Override
		public View onCreateActionView() {
			return mShareActionProvider.onCreateActionView();
		}

		@Override
		public boolean onPerformDefaultAction() {
			return mShareActionProvider.onPerformDefaultAction();
		}

		@Override
		public boolean hasSubMenu() {
			return mShareActionProvider.hasSubMenu();
		}
		
		public void setOnShareTargetSelectedListener(ShareActionProvider.OnShareTargetSelectedListener listener){
			mShareActionProvider.setOnShareTargetSelectedListener(listener);
		}
		
		public void setShareIntent(Intent shareIntent){
			mShareActionProvider.setShareIntent(shareIntent);
		}
		
	}

	public interface Callback {
		public void itemSelected(long id);

		public void itemDeleted(long id);
	}

	private LocationDatabase db;

	private ListView listView;
	private ListAdapter adapter;

	private List<Callback> callbacks;

	private final static int MENU_REMOVE_SELECTED_LOCATIONS = 10;
	private final static int MENU_SHARE_SELECTED_LOCATIONS = 11;
	
	private File exportFolder = Environment.getExternalStorageDirectory();
	private File exportFile = new File(exportFolder,"orte.zip");
	
	private ProgressDialog exportProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callbacks = new ArrayList<Callback>();
		db = new LocationDatabase(this.getActivity());
		setContentView(R.layout.fragment_location_list);
		setHasOptionsMenu(true);
	}

	@Override
	protected void initUIElements() {
		listView = (ListView) findViewById(R.id.listViewLoactions);
		listView.setOnItemClickListener(this);
	}

	public void addCallback(Callback callback) {
		if (callback != null) {
			callbacks.add(callback);
		}
	}

	public void removeCallback(Callback callback) {
		if (callback != null) {
			callbacks.remove(callback);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long listId) {
		long id = ((TargetLocation) adapter.getItem(position)).getId();
		for (Callback c : callbacks) {
			c.itemSelected(id);
		}
	}

	public ListAdapter getListAdapter() {
		return adapter;
	}

	private void deleteSelectedLocations() {
		List<TargetLocation> locations = getSelectedLocations();
		deleteLocations(locations);

	}

	public void deleteLocations(List<TargetLocation> locations) {
		db.open();
		for (TargetLocation t : locations) {
			db.deleteLocation(t);
			for (Callback c : callbacks) {
				c.itemDeleted(t.getId());
			}
		}
		adapter = new LocationListAdapter(this.getActivity(), -1, -1,
				db.getAllLocations());
		db.close();
		listView.setAdapter(adapter);
	}

	public List<TargetLocation> getSelectedLocations() {
		List<TargetLocation> locations = new ArrayList<TargetLocation>(
				adapter.getCount());
		for (int i = 0; i < adapter.getCount(); i++) {
			TargetLocation tempLocation = (TargetLocation) adapter.getItem(i);
			if (tempLocation.isSelected()) {
				locations.add(tempLocation);
			}
		}
		return Collections.unmodifiableList(locations);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.d("UTM", "Create Options Menu called from fragment");
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.FIRST,
				getString(R.string.submenu_edit));
		subMenu.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		MenuItem removeItem = subMenu.add(Menu.NONE,
				MENU_REMOVE_SELECTED_LOCATIONS, Menu.NONE,
				R.string.delete_marked_locations);
		MenuItem shareItem = subMenu.add(Menu.NONE,
				MENU_SHARE_SELECTED_LOCATIONS, Menu.NONE, R.string.menu_share_locations);

		// removeItem.setActionProvider(new RemoveActionProvider(this));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_REMOVE_SELECTED_LOCATIONS:
			deleteSelectedLocations();
			return true;
		case MENU_SHARE_SELECTED_LOCATIONS:
			shareSelectedLocations();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void shareSelectedLocations(){
		exportSelectedLocationsToArchive();
	}
	
	private void sendExportedLocations(){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("application/zip");
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(exportFile));
		startActivity(Intent.createChooser(i, getString(R.string.choose_sending_intent)));
	}
	
	private void exportSelectedLocationsToArchive(){
		if(exportFile.exists()){
			exportFile.delete();
		}
		String exportPath = exportFile.getAbsolutePath();
		List<TargetLocation> locations = getSelectedLocations();
		LocationExporter exporter = new LocationExporter();
		exporter.setOutputPath(exportPath);
		exporter.addListener(this);
		exporter.execute(locations);
		
	}

	@Override
	public void exportStarted() {
		exportProgressDialog = new ProgressDialog(this.getActivity());
		exportProgressDialog.setCancelable(false);
		exportProgressDialog.setMessage(getString(R.string.progress_dialog_exporting_entries));
		
	}

	@Override
	public void exportFinished() {
		
		exportProgressDialog.dismiss();
		sendExportedLocations();
		
	}

	@Override
	public void updateProgress(int max, int progress) {
		exportProgressDialog.setMax(max);
		exportProgressDialog.setProgress(progress);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		db.open();
		adapter = new LocationListAdapter(this.getActivity(), 0, 0,
				db.getAllLocations());
		db.close();
		listView.setAdapter(adapter);
	}

}
