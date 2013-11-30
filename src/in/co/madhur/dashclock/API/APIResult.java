package in.co.madhur.dashclock.API;

import com.google.api.services.analytics.model.GaData;

import in.co.madhur.dashclock.Consts.API_STATUS;

public class APIResult
{
	private String errorMessage;
	private API_STATUS status;
	private GaData result;
	private String resultMessage;
	
	public APIResult(API_STATUS status, GaData result, String errorMessage)
	{
		if(status==API_STATUS.FAILURE)
			this.errorMessage=errorMessage;
		else if(status==API_STATUS.SUCCESS)
			this.result=result;
		
	}
	
	public APIResult(API_STATUS status, String result)
	{
		if(status==API_STATUS.FAILURE)
			this.errorMessage=result;
		else if(status==API_STATUS.SUCCESS)
			this.resultMessage=result;
		
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	
	public API_STATUS getStatus()
	{
		return status;
	}
	
	public void setStatus(API_STATUS status)
	{
		this.status = status;
	}
	
	public GaData getResult()
	{
		return result;
	}
	
	public void setResult(GaData result)
	{
		this.result = result;
	}

	public String getResultMessage()
	{
		return resultMessage;
	}

	public void setResultMessage(String resultMessage)
	{
		this.resultMessage = resultMessage;
	}

}
