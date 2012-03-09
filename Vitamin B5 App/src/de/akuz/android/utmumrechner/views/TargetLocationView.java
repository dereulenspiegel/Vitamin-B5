package de.akuz.android.utmumrechner.views;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.TargetLocation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TargetLocationView extends LinearLayout implements OnCheckedChangeListener{
	
	private Context mContext;
	
	private TextView textViewUTM;
	private TextView textViewName;
	
	private CheckBox checkBox;
	private TargetLocation currentLocation;

	public TargetLocationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TargetLocationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TargetLocationView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View baseLayout = inflater.inflate(R.layout.location_view, this);
		
		textViewUTM = (TextView)baseLayout.findViewById(R.id.textViewUTM);
		textViewName = (TextView)baseLayout.findViewById(R.id.textViewName);
		checkBox = (CheckBox)baseLayout.findViewById(R.id.checkBox);
		checkBox.setOnCheckedChangeListener(this);
	}
	
	public void setName(String name){
		textViewName.setText(name);
	}
	
	public void setUTM(String utm){
		textViewUTM.setText(utm);
	}
	
	public void bindTargetLocation(TargetLocation location){
		currentLocation = location;
		setName(location.getName());
		setUTM(location.getMgrsCoordinate());
		checkBox.setChecked(location.isSelected());
	}
	
	public boolean isChecked(){
		return checkBox.isChecked();
	}
	
	public void setChecked(boolean checked){
		checkBox.setChecked(checked);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		currentLocation.setSelected(isChecked);	
	}

}
