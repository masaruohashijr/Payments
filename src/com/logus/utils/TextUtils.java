package com.logus.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TextUtils {
	public static LocalDate getDataPrevisao(String dayOfMonth, String strDataPrevisao, DateTimeFormatter format) {
		String month = strDataPrevisao.substring(3,5);
		String year = strDataPrevisao.substring(6,10);
		LocalDate dtPrevisao = null;
		dayOfMonth = padLeftZeros(dayOfMonth, 2);
		if(Integer.valueOf(dayOfMonth)>=28) {
			dayOfMonth = "01";
			dtPrevisao = LocalDate.parse(dayOfMonth + "/" + month + "/" + year,format);
			int lengthOfMonth = dtPrevisao.lengthOfMonth();
			dtPrevisao = LocalDate.parse(lengthOfMonth+"/" + month + "/" + dtPrevisao.getYear(),format);
		} else {
			dtPrevisao = LocalDate.parse(dayOfMonth + "/" + month + "/" + year,format);
		}
		return dtPrevisao;
	}

	public static String padLeftZeros(String inputString, int length) {
	    if (inputString.length() >= length) {
	        return inputString;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < length - inputString.length()) {
	        sb.append('0');
	    }
	    sb.append(inputString);

	    return sb.toString();
	}
}
