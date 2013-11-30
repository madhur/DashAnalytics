package in.co.madhur.dashclock;

import in.co.madhur.dashclock.API.GNewProfile;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class MyBaseAdapter extends BaseAdapter
{

	protected ArrayList<GNewProfile> items;
	protected Context context;
	
	public MyBaseAdapter(ArrayList<GNewProfile> items, Context context)
	{
		this.items=items;
		this.context=context;
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	
	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	protected final static LayoutInflater GetInflater(Context context)
	{

		return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


}
