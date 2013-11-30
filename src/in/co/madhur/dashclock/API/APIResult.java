package in.co.madhur.dashclock.API;

import com.google.api.services.analytics.model.GaData;

import in.co.madhur.dashclock.Consts.API_STATUS;

public  class APIResult
{
	protected String errorMessage;
	protected API_STATUS status;
	
	public APIResult()
	{
		
		
	}
	
	public APIResult(String errorMessage)
	{
		this.status=API_STATUS.FAILURE;
		this.errorMessage=errorMessage;
		
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
	

}
