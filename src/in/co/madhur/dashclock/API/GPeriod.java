package in.co.madhur.dashclock.API;

import in.co.madhur.dashclock.Consts.APIPeriod;

public class GPeriod extends GType
{
	public GPeriod()
	{
		
	}

	public GPeriod(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GPeriod(APIPeriod Id, String Name)
	{
		super(Id.toString(), Name);
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
