package in.co.madhur.dashclock.dashadsense;

import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import in.co.madhur.dashclock.API.APIResult;
import in.co.madhur.dashclock.Consts.API_STATUS;

public class AdSenseAPIResult extends APIResult
{
	private AdsenseReportsGenerateResponse result;
	
	public AdSenseAPIResult(AdsenseReportsGenerateResponse result)
	{
			this.setResult(result);
			this.status=API_STATUS.SUCCESS;
	}

	public AdsenseReportsGenerateResponse getResult()
	{
		return result;
	}

	public void setResult(AdsenseReportsGenerateResponse result)
	{
		this.result = result;
	}
	
}
