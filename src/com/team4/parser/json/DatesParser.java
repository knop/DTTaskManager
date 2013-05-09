package com.team4.parser.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team4.exceptions.ErrorCode;
import com.team4.type.TDate;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.parser.IJsonObjectParser;
import com.team4.utils.type.IBaseType;
import com.team4.utils.type.T4List;

public class DatesParser implements IJsonObjectParser {

	public T4List<TDate> parse(JSONArray array) throws JSONException {
		T4List<TDate> list = new T4List<TDate>();
		for (int i=0; i<array.length(); i++) {
			TDate date = new TDate();
			date.setDate(array.getString(i));
			list.add(date);
		}
		return list;
	}

	@Override
	public IBaseType parse(JSONObject json) throws T4Exception {
		T4List<TDate> dateList = null;
		try {
			if (json.has("date")) {
				JSONArray array = json.getJSONArray("date");	
				dateList = this.parse(array);
			}
		} catch (JSONException e) { 
			throw new T4Exception(ErrorCode.PARSE_ERROR_FORMAT_INVALID, "JSON格式错误");
		}
		return dateList;
	}
}