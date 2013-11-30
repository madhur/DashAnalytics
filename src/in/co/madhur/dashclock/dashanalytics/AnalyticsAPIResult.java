package in.co.madhur.dashclock.dashanalytics;

import com.google.api.services.analytics.model.GaData;

import in.co.madhur.dashclock.Consts.API_STATUS;
import in.co.madhur.dashclock.API.APIResult;

public class AnalyticsAPIResult extends APIResult
{
	private GaData result;
	
	public AnalyticsAPIResult(GaData result)
	{
			this.result=result;
			this.status=API_STATUS.SUCCESS;
	}
	
	public GaData getResult()
	{
		return result;
	}
	
	public void setResult(GaData result)
	{
		this.result = result;
	}

	

}
