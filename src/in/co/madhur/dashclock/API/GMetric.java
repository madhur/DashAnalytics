package in.co.madhur.dashclock.API;

import in.co.madhur.dashclock.Consts.ANALYTICS_METRICS;

public class GMetric extends GType
{
	
	public GMetric()
	{
		
	}

	public GMetric(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GMetric(ANALYTICS_METRICS Id, String Name)
	{
		super(Id.toString(), Name);
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
