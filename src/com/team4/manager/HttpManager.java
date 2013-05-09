package com.team4.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.team4.parser.json.DatesParser;
import com.team4.parser.json.JsonParserImpl;
import com.team4.parser.json.StudentParser;
import com.team4.parser.json.TasksParser;
import com.team4.task.DownloadAsyncTask;
import com.team4.task.DownloadAsyncTask.OnDownloadListener;
import com.team4.type.TDate;
import com.team4.type.TStudent;
import com.team4.type.TTask;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.http.HttpUtility;
import com.team4.utils.type.T4List;

public class HttpManager {

	private final static String host = "http://dreamtown.team4.us";
	private final static String userAgent = "Team4.US/ZMTTaskManager/Android";
	
	//API接口名称
	//登录
	private final static String LOGIN = "/taskapp/login/";
	//任务日期列表
	private final static String TASK_DATE_LIST = "/taskapp/taskdatelist/";
	//任务列表
	private final static String TASK_LIST = "/taskapp/tasklist";
	
	//接口类型名称	
	private static HttpManager sInstance;
	
	private HttpManager() {
		
	}
	
	public static HttpManager instance() {
		if (sInstance == null) {
			sInstance = new HttpManager();
		} 
		
		return sInstance;
	}
	
	//Http请求调用
	public TStudent login(Context context, String username, String password) throws T4Exception {
		List<BasicNameValuePair> params = getParamList(
				new BasicNameValuePair("username", username), 
				new BasicNameValuePair("password", password));
		HttpGet get = HttpUtility.createHttpGet(HttpManager.fillUrl(LOGIN), userAgent, params);
		StudentParser parser = new StudentParser();
		return (TStudent)HttpUtility.executeHttpRequest(context, get, new JsonParserImpl(parser));
	}
	
	@SuppressWarnings("unchecked")
	public T4List<TDate> getDateList(Context context, String studentId) throws T4Exception {
		List<BasicNameValuePair> params = getParamList(
				new BasicNameValuePair("student_id", studentId));
		HttpGet get = HttpUtility.createHttpGet(HttpManager.fillUrl(TASK_DATE_LIST), userAgent, params);
		DatesParser parser = new DatesParser();	
		return (T4List<TDate>)HttpUtility.executeHttpRequest(context, get, new JsonParserImpl(parser));
	}
	
	@SuppressWarnings("unchecked")
	public T4List<TTask> getTaskList(Context context, String studentId, String date) throws T4Exception {
		List<BasicNameValuePair> params = getParamList(
				new BasicNameValuePair("student_id", studentId),
				new BasicNameValuePair("date", date));
		HttpGet get = HttpUtility.createHttpGet(HttpManager.fillUrl(TASK_LIST), userAgent, params);
		TasksParser parser = new TasksParser();	
		return (T4List<TTask>)HttpUtility.executeHttpRequest(context, get, new JsonParserImpl(parser));
	}
	
	public DownloadAsyncTask downloadFile(String relativeUrl, String savedPath, OnDownloadListener listener) {
		DownloadAsyncTask task = new DownloadAsyncTask(listener);
		task.execute(HttpManager.fillUrl(relativeUrl), savedPath);
		return task;
	}
	
	//私有函数
	public static String fillUrl(String api) {
		return host + api;
	}
	
	private List<BasicNameValuePair> getParamList(BasicNameValuePair... nameValuePairs) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (BasicNameValuePair obj : nameValuePairs) {
			if (obj.getValue() == null || obj.getValue().length() <= 0)
				continue;
			params.add(obj);
		}

		Collections.sort(params, new Comparator<BasicNameValuePair>() {

			public int compare(BasicNameValuePair lhs, BasicNameValuePair rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});
		return params;
	}
}
