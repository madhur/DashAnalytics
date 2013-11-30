package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.Consts;
import in.co.madhur.dashclock.MyBaseAdapter;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.GNewProfile;
import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends MyBaseAdapter
{
	
	public MyAdapter(ArrayList<GNewProfile> items, Context context)
	{
		super(items, context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi=convertView;
		if(vi==null)
			vi=GetInflater(context).inflate(R.layout.list_row, null);
		
		GNewProfile newProfile=(GNewProfile) this.getItem(position);
		TextView propertyText=(TextView) vi.findViewById(R.id.property_name);
		TextView profileText=(TextView) vi.findViewById(R.id.profile_name);
		ImageView kindView=(ImageView) vi.findViewById(R.id.AccountTypeImage);
		
		int resID;
	    
		if(newProfile.isApp())
			resID=context.getResources().getIdentifier(Consts.HARDWARE_PHONE, "drawable",  context.getPackageName());
		else
			resID=context.getResources().getIdentifier(Consts.LOCATION_WEB_SITE, "drawable",  context.getPackageName());

		kindView.setImageResource(resID);
		
		
		propertyText.setText(newProfile.getPropertyName());
		profileText.setText(newProfile.getProfileName());
		
		return vi;
	}
	
}
