package de.akuz.android.utmumrechner.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.akuz.android.utmumrechner.data.TargetLocation;

import android.os.AsyncTask;
import android.util.Log;

public class LocationExporter extends AsyncTask<List<TargetLocation>, Integer, Boolean> {
	
	public interface ExportListener{
		public void exportStarted();
		public void exportFinished();
		public void updateProgress(int max, int progress);
	}
	
	private List<TargetLocation> locations;
	private ZipOutputStream zOut;
	private final static int BUFFER_SIZE = 2048;
	
	private List<ExportListener> listener = new ArrayList<LocationExporter.ExportListener>();
	
	private String outPath = "";
	
	private int locationCount = 0;
	@Override
	protected Boolean doInBackground(List<TargetLocation>... arguments) {
		locations = arguments[0];
		locationCount = locations.size();
		try {
			createZipFile(outPath);
			addBytesToZip("orte.csv", generateCSVExport());
			int i = 0;
			for(TargetLocation l : locations){
				if(l.getPictureUrl()!=null){
					addZipFile(l.getPictureUrl());
				}
				i++;
				onProgressUpdate(i);
			}
			closeZip();
			return true;
		} catch (FileNotFoundException e) {
			Log.e("UTM","Error while exporting",e);
			return false;
		} catch (IOException e) {
			Log.e("UTM","Error while exporting",e);
			return false;
		}
		
	}
	
	public void setOutputPath(String path){
		outPath = path;
	}
	
	private byte[] generateCSVExport(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		CSVWriter csvWriter = new CSVWriter(writer);
		for(TargetLocation l : locations){
			String[] line = new String[4];
			line[0] = l.getName();
			line[1] = l.getMgrsCoordinate();
			line[2] = l.getDescription();
			line[3] = l.getPictureUrl();
			csvWriter.writeNext(line);
			csvWriter.flush();
		}
		csvWriter.close();
		
		return out.toByteArray();
	}
	
	private void createZipFile(String path) throws FileNotFoundException{
		FileOutputStream dest = null;
		dest = new FileOutputStream(path);
		zOut = new ZipOutputStream(new BufferedOutputStream(dest));
	}
	
	private void addZipFile(String name) throws IOException{
		File file = new File(name);
		FileInputStream fIn = new FileInputStream(file);
		BufferedInputStream origin = new BufferedInputStream(fIn);
		ZipEntry entry = null;
		if(name.endsWith(".jpg")){
			entry = new ZipEntry("images/"+file.getName());
		} else {
			entry = new ZipEntry(file.getName());
		}
		zOut.putNextEntry(entry);
		byte[] buffer = new byte[BUFFER_SIZE];
		int count = 0;
		while((count = origin.read(buffer)) != -1){
			zOut.write(buffer);
		}
		origin.close();
	}
	
	private void addBytesToZip(String name, byte[] bytes) throws IOException{
		ZipEntry entry = null;
		if(name.endsWith(".jpg")){
			entry = new ZipEntry("images/"+name);
		} else {
			entry = new ZipEntry(name);
		}
		zOut.putNextEntry(entry);
		zOut.write(bytes);
	}
	
	private void closeZip() throws IOException{
		zOut.close();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		for(ExportListener l : listener){
			l.exportFinished();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		for(ExportListener l : listener){
			l.exportStarted();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		for(ExportListener l : listener){
			l.updateProgress(locationCount, values[0]);
		}
	}
	
	public void addListener(ExportListener listener){
		if(listener!=null){
			this.listener.add(listener);
		}
	}
	
	public void removeListener(ExportListener listener){
		if(listener!=null){
			this.listener.remove(listener);
		}
	}

}
