package com.midhunarmid.movesapi.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;

/**
 * A collection of utility functions, commonly used throughout this application
 * @author Midhu
 */
public class Utilities {
	
	/** Use this method to read an {@link InputStream} to a {@link String}
	 * @param stream : The input stream to read
	 * @return String representation of data read from the provided input stream
	 * @throws Exception
	 */
	public static String readStream(InputStream stream) throws Exception {
		if (stream == null) {
			return "";
		}
		int ch;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((ch = stream.read()) != -1) {
			bos.write(ch);
		}
		stream.close();
		return new String(bos.toByteArray(), "UTF-8");
	}
	
	/** Pass a {@link HashMap} of parameters and this method will return them in a URL Encoded {@link String} format
	 * @param parameters : Required URL parameters to encode
	 * @return and encoded URL parameter list as {@link String}
	 */
	public static String encodeUrl(HashMap<String , String> parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			try {
				sb.append(URLEncoder.encode(key,"UTF-8") + "=" + URLEncoder.encode(parameters.get(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
		return sb.toString();
	}
	
	/** Pass a URL {@link String} and this method will return the parameters in a {@link HashMap} format
	 * @param completeURL to parse and decode parameters
	 * @return A {@link HashMap} of decoded parameters from the URL
	 */
	public static HashMap<String , String> decodeUrl(String completeURL) {
		HashMap<String , String> params = new HashMap<String, String>();
		if (completeURL == null || completeURL.length() <= 0 || !completeURL.contains("?")) {
			return params;
		}
		completeURL = completeURL.substring(completeURL.indexOf("?") + 1);
		for (String keyValuePair : completeURL.split("&") ) {
			params.put(keyValuePair.split("=")[0], keyValuePair.split("=")[1]);
		}
		return params;
	}
	
	/** Use this method to convert the milliseconds value to date (eg. format : yyyyMMdd)
	 * @param milis : Time in milliseconds
	 * @param format : Required date format
	 * @return A {@link String} representation of converted date in given format
	 */
	public static String timeMilisToString(long milis, String format) throws Exception {
		SimpleDateFormat sd = new SimpleDateFormat(format, Locale.getDefault());
		Calendar calendar   = Calendar.getInstance();
		calendar.setTimeInMillis(milis);
		return sd.format(calendar.getTime());
	}
	
	/**
	 * Use this method to get convert a {@link String} representation of Date into milliseconds.
	 * @param dateValue : Date to be converted, as String
	 * @param currentFormat : Current format of this Date
	 * @param def : Default value to be returned, if an error occurred
	 * @return The converted date/time value in milliseconds
	 */
	@SuppressLint("SimpleDateFormat")
	public static Long getTimeInMillis(String dateValue, String currentFormat, long def) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
			Date dt;
			dt = sdf.parse(dateValue);
			return dt.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}
}
