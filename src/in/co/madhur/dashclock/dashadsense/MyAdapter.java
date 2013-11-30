package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.MyBaseAdapter;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.GNewProfile;
import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
			vi=GetInflater(context).inflate(R.layout.adsense_list_row, null);
		
		GNewProfile newProfile=(GNewProfile) this.getItem(position);
		TextView propertyText=(TextView) vi.findViewById(R.id.property_name_adsense);
		
		propertyText.setText(newProfile.getAccountName());
		
		return vi;
	}
	
}
