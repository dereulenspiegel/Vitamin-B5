package de.akuz.android.utmumrechner.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.LocationExporter;
import de.akuz.android.utmumrechner.utils.StringUtils;
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

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	public static class FragmentState {
		public int listPosition;
		public List<Long> markedPositions;
	}

	public interface Callback {
		public void itemSelected(long id);

		public void itemDeleted(long id);
	}

	private LocationDatabase db;

	private ListView listView;
	private LocationListAdapter adapter;

	private List<Callback> callbacks;

	private final static int MENU_REMOVE_SELECTED_LOCATIONS = 10;
	private final static int MENU_SHARE_SELECTED_LOCATIONS = 11;

	private File exportFolder = Environment.getExternalStorageDirectory();
	private File exportFile = new File(exportFolder, "orte.zip");

	private ProgressDialog exportProgressDialog;

	private FragmentState lastState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callbacks = new ArrayList<Callback>();
		db = new LocationDatabase(this.getActivity());

		setContentView(R.layout.fragment_location_list);
		setHasOptionsMenu(true);
		db.open();
		List<TargetLocation> locations = db.getAllLocations();
		db.close();
		if (lastState != null) {
			for (TargetLocation l : locations) {
				if(lastState.markedPositions.contains(l.getId())){
					Log.d("UTM","!!!!!!!Setting selection");
					l.setSelected(true);
				}
			}
		}
		adapter = new LocationListAdapter(this.getActivity(), 0, 0, locations);
	}

	@Override
	protected void initUIElements() {
		Log.d("UTM", "Init UI Elements");
		listView = (ListView) findViewById(R.id.listViewLoactions);
		listView.setOnItemClickListener(this);
		
		listView.setAdapter(adapter);
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
			deleteImage(t.getPictureUrl());
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

	private void deleteImage(String path) {
		if (StringUtils.isEmtpy(path)) {
			return;
		}
		File image = new File(path);
		if (image.exists()) {
			image.delete();
		}
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
				MENU_SHARE_SELECTED_LOCATIONS, Menu.NONE,
				R.string.menu_share_locations);

		// removeItem.setActionProvider(new RemoveActionProvider(this));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_REMOVE_SELECTED_LOCATIONS:
			createDeleteDialog().show();
			return true;
		case MENU_SHARE_SELECTED_LOCATIONS:
			shareSelectedLocations();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void shareSelectedLocations() {
		exportSelectedLocationsToArchive();
	}

	private void sendExportedLocations() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("application/zip");
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(exportFile));
		startActivity(Intent.createChooser(i,
				getString(R.string.choose_sending_intent)));
	}

	private void exportSelectedLocationsToArchive() {
		if (exportFile.exists()) {
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
		exportProgressDialog
				.setMessage(getString(R.string.progress_dialog_exporting_entries));

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
		List<TargetLocation> locations = db.getAllLocations();
		db.close();
		if (lastState != null) {
			for (TargetLocation l : locations) {
				if(lastState.markedPositions.contains(l.getId())){
					Log.d("UTM","!!!!!!!Setting selection");
					l.setSelected(true);
				}
			}
		}
		adapter = new LocationListAdapter(this.getActivity(), 0, 0, locations);
		listView.setAdapter(adapter);
		lastState = null;
	}

	private Dialog createDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.really_delete_locations);
		builder.setCancelable(true);
		builder.setNegativeButton(R.string.No,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteSelectedLocations();
						dialog.dismiss();

					}
				});

		return builder.create();
	}

	public FragmentState getCurrentFragmentState() {
		FragmentState state = new FragmentState();
		state.listPosition = listView.getFirstVisiblePosition();
		List<Long> markedPositions = new ArrayList<Long>();
		for (int i = 0; i < listView.getCount(); i++) {
			TargetLocation l = (TargetLocation) listView.getItemAtPosition(i);
			if (l.isSelected()) {
				markedPositions.add(l.getId());
			}
		}
		state.markedPositions = markedPositions;
		return state;
	}

	public void restoreState(FragmentState state) {
		if (state != null && listView != null && adapter != null) {
			Log.d("UTM", "Restoring fragment state");
			listView.setSelection(state.listPosition);
			db.open();
			List<TargetLocation> locations = db.getAllLocations();
			db.close();
			if (state != null) {
				for (TargetLocation l : locations) {
					if(state.markedPositions.contains(l.getId())){
						Log.d("UTM","!!!!!!!Setting selection");
						l.setSelected(true);
					}
				}
			}
			adapter = new LocationListAdapter(this.getActivity(), 0, 0, locations);
			

			listView.setAdapter(adapter);
			lastState = null;
		}
	}

	public void setFragmentState(FragmentState state) {
		if (state != null) {
			Log.d("UTM", "Fragment setting last state");
			lastState = state;
		}
	}

}
