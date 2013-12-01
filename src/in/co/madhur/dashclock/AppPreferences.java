package in.co.madhur.dashclock;

import in.co.madhur.dashclock.API.GNewProfile;

import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public abstract class AppPreferences
{

	public SharedPreferences sharedPreferences;
	protected Context context;

	public enum Keys
	{

		USER_NAME("pref_user_name"),
		CONFIGURATION("pref_config"),
		ACCOUNT_ID("account_id"),
		ACCOUNT_NAME("account_name"),
		PROPERTY_ID("property_id"),
		PROPERTY_NAME("property_name"),
		PROFILE_ID("profile_id"),
		PROFILE_NAME("profile_name"),
		METRIC_ID("metric_id"),
		PERIOD_ID("period_id"),
		ADSENSE_PERIOD_ID("adsense_period_id"),
		USE_LOCAL_TIME("pref_adsense_usetimezonee"),
		SHOW_CURRENCY("pref_showcurrency"),
		SHOW_PAGE_VIEWS("pref_showpageviews"),
		SHOW_CLICKS("pref_showclicks"),
		SHOW_PAGE_CTR("pref_showpagectr"),
		SHOW_PAGE_RPM("pref_showpagerpm"),
		SHOW_CPC("pref_showcpc"),
		AUTH_TOKEN("pref_auth_token");

		public final String key;

		private Keys(String key)
		{
			this.key = key;

		}

	};

	public void setAuthToken(String token)
	{

		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.AUTH_TOKEN.key, token);
		editor.commit();
	}

	public String getAuthToken()
	{

		return sharedPreferences.getString(Keys.AUTH_TOKEN.key, "");

	}

	public void setUserName(String username)
	{

		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.USER_NAME.key, username);
		editor.commit();
	}

	public void saveConfigData(ArrayList<GNewProfile> gAccounts, String accountName)
			throws JsonProcessingException
	{
		String accountsjson;
		ObjectMapper mapper = new ObjectMapper();
		accountsjson = mapper.writeValueAsString(gAccounts);
		SharedPreferences.Editor editor = this.sharedPreferences.edit();
		editor.putString(Keys.CONFIGURATION.key + accountName, accountsjson);
		editor.commit();
	}

	public ArrayList<GNewProfile> getConfigData(String accountName)
			throws JsonParseException, JsonMappingException, IOException
	{
		String json = sharedPreferences.getString(Keys.CONFIGURATION.key
				+ accountName, "");
		ArrayList<GNewProfile> gAccounts = null;
		if (!TextUtils.isEmpty(json))
		{
			ObjectMapper mapper = new ObjectMapper();
			gAccounts = mapper.readValue(json, new TypeReference<ArrayList<GNewProfile>>()
			{
			});
		}

		return gAccounts;

	}
	
	public boolean getboolMetaData(Keys key)
	{
		boolean defValue=false;
		
		return sharedPreferences.getBoolean(key.key, defValue);
	}
	
	public boolean isLocalTime()
	{
		return sharedPreferences.getBoolean(Keys.USE_LOCAL_TIME.key, true);
	}
	
	public boolean isShowcurrency()
	{
		return sharedPreferences.getBoolean(Keys.SHOW_CURRENCY.key, true);
		
	}
	
	

	public void setMetadata(Keys key, String value)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(key.key, value);
		editor.commit();
	}

	public void setMetadataMultiple(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.PROPERTY_ID.key, propertyId);
		editor.putString(Keys.PROPERTY_NAME.key, propertyName);
		editor.putString(Keys.PROFILE_ID.key, profileId);
		editor.putString(Keys.PROFILE_NAME.key, profileName);
		editor.commit();
	}
	
	public void setMetadataMultiple(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName, String accountEmail)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.PROPERTY_ID.key, propertyId);
		editor.putString(Keys.PROPERTY_NAME.key, propertyName);
		editor.putString(Keys.PROFILE_ID.key, profileId);
		editor.putString(Keys.PROFILE_NAME.key, profileName);
		
		editor.putString(Keys.USER_NAME.key, accountEmail);
		editor.commit();
	}

	public String getMetadata(Keys key)
	{
		String defValue = "";

		if (key == Keys.METRIC_ID)
			defValue = Defaults.METRIC_ID;
		else if (key == Keys.PERIOD_ID)
			defValue = Defaults.PERIOD_ID;
		else if (key==Keys.ADSENSE_PERIOD_ID)
			defValue=Defaults.PERIOD_ID;

		return sharedPreferences.getString(key.key, defValue);
	}

	public String getUserName()
	{

		return sharedPreferences.getString(Keys.USER_NAME.key, "");

	}

	public void setMetadataMultiple(String accountId, String accountName, String accountEmail)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.USER_NAME.key, accountEmail);
		editor.commit();
		
	}

}
