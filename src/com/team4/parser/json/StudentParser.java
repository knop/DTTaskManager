package com.team4.parser.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.team4.exceptions.ErrorCode;
import com.team4.type.TStudent;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.parser.IJsonObjectParser;
import com.team4.utils.type.IBaseType;

public class StudentParser implements IJsonObjectParser {

	@Override
	public IBaseType parse(JSONObject json) throws T4Exception {
		try {
			TStudent obj = new TStudent();
			if (json.has("student_id")) {
				obj.setId(json.getInt("student_id"));
			}

			if (json.has("name")) {
				obj.setName(json.getString("name"));
			}
			
			return obj;
		} catch (JSONException e) {
			throw new T4Exception(ErrorCode.PARSE_ERROR_FORMAT_INVALID, "JSON格式错误");
		}
	}
	
}