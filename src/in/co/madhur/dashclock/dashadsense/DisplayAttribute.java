package in.co.madhur.dashclock.dashadsense;

import java.text.NumberFormat;

import android.text.TextUtils;
import android.util.Log;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.Consts.ATTRIBUTE_TYPE;

public class DisplayAttribute
{
	private String value;
	private ATTRIBUTE_TYPE type;
	private String currencyCode;

	public DisplayAttribute(String val, ATTRIBUTE_TYPE type)
	{
		this.value = val;
		this.type = type;
	}
	
	public DisplayAttribute(String val, ATTRIBUTE_TYPE type, String currencyCode)
	{
		this.value = val;
		this.type = type;
		this.currencyCode=currencyCode;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public ATTRIBUTE_TYPE getType()
	{
		return type;
	}

	public void setType(ATTRIBUTE_TYPE type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return getFormattedValue();
	}

	public String getFormattedValue()
	{
		if(TextUtils.isEmpty(value))
		{
			Log.d(App.TAG_ADSENSE, "Encountered null value");
			value="0";
		}
		
		try
		{
			switch (type)
			{
				case CURRENCY:
					
					return NumberFormat.getNumberInstance().format(Double.parseDouble(value));

				case NUMBER:
					return NumberFormat.getNumberInstance().format(Double.parseDouble(value));

				case PERCENTAGE:
					NumberFormat ni = NumberFormat.getPercentInstance();
					ni.setMinimumFractionDigits(2);
					return ni.format(Double.parseDouble(value));

				default:
					return value;

			}
		}
		catch (NumberFormatException e)
		{
			Log.e(App.TAG_ADSENSE, e.getMessage());
		}

		return value;
	}

	public String getCurrencyCode()
	{
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode)
	{
		this.currencyCode = currencyCode;
	}

}
