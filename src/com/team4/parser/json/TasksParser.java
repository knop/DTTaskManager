package com.team4.parser.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team4.exceptions.ErrorCode;
import com.team4.type.TTask;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.parser.IJsonArrayParser;
import com.team4.utils.type.IBaseType;
import com.team4.utils.type.T4List;
import com.team4.utils.util.T4Log;

public class TasksParser implements IJsonArrayParser {

	public TTask parseChild(JSONObject json) throws JSONException {
		TTask task = new TTask();
		if (json.has("job")) {
			String job = json.getString("job");
			task.setJob(job);
			T4Log.d(job);
		}
		
		if (json.has("attachment")) {
			String attachment = json.getString("attachment");
			task.setAttachment(attachment);
			T4Log.d(attachment);
		}
		return task;
	}

	@Override
	public IBaseType parse(JSONArray array) throws T4Exception {
		T4List<TTask> list = new T4List<TTask>();
		try {
			for (int i=0; i<array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				TTask task = this.parseChild(object);
				list.add(task);
			}
		} catch (JSONException e) { 
			throw new T4Exception(ErrorCode.PARSE_ERROR_FORMAT_INVALID, "JSON格式错误");
		}
		return list;
	}
}