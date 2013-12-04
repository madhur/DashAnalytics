package in.co.madhur.dashclock;

import in.co.madhur.dashclock.Consts.ATTRIBUTE_TYPE;

import java.text.NumberFormat;

import android.text.TextUtils;
import android.util.Log;


public class DisplayAttribute
{
	private String value;
	private ATTRIBUTE_TYPE type;
	private String currencyCode;

	public DisplayAttribute(String val, String type)
	{
		this.value = val;
		this.type = ATTRIBUTE_TYPE.valueOf(type);
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
				case METRIC_CURRENCY:
					
					return NumberFormat.getNumberInstance().format(Double.parseDouble(value));

				case INTEGER:
					return NumberFormat.getNumberInstance().format(Double.parseDouble(value));
					
				case METRIC_TALLY:
					return NumberFormat.getNumberInstance().format(Double.parseDouble(value));
					
				case FLOAT:
					NumberFormat n = NumberFormat.getNumberInstance();
					//n.setMinimumFractionDigits(2);
					n.setMaximumFractionDigits(2);
					return n.format(Double.parseDouble(value));
					
				case METRIC_RATIO:
					NumberFormat ni = NumberFormat.getPercentInstance();
					//ni.setMinimumFractionDigits(2);
					ni.setMaximumFractionDigits(2);
					return ni.format(Double.parseDouble(value));
					
				case PERCENT:
					NumberFormat nii = NumberFormat.getPercentInstance();
					//nii.setMinimumFractionDigits(2);
					nii.setMaximumFractionDigits(2);
					return nii.format(Double.parseDouble(value)/100);

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
	
//	private static String fmt(double d)
//	{
//		if (d == (int) d)
//			return String.format("%d", (int) d);
//		else
//		{
//			return new DecimalFormat("#.##").format(d);
//		}
//	}

}
