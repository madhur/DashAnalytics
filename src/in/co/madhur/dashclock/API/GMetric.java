package in.co.madhur.dashclock.API;

import in.co.madhur.dashclock.Consts.APIMetrics;

public class GMetric extends GType
{
	
	public GMetric()
	{
		
	}

	public GMetric(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GMetric(APIMetrics Id, String Name)
	{
		super(Id.toString(), Name);
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
