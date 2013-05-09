package com.team4.task;

import android.content.Context;
import android.os.AsyncTask;

import com.team4.manager.HttpManager;
import com.team4.type.TTask;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.type.T4List;

public class GetTasksAsyncTask extends AsyncTask<String, Void, T4List<TTask>>{
	
	private T4Exception mException;
	private Context mContext;
	private IGetTasksTaskCompletation mListener;
	
	public GetTasksAsyncTask(Context context, IGetTasksTaskCompletation listener) {
		mContext = context;
		mListener = listener;
	}
	
	@Override
	public T4List<TTask> doInBackground(String... params) {
		String studentId = params[0];
		String date = params[1];
		
		T4List<TTask> tasks = null;
	
		try {
			tasks = HttpManager.instance().getTaskList(mContext, studentId, date);
		} catch (T4Exception ex) {
			mException = ex;
		} 
	
		return tasks;
	}
	
	@Override
	public void onPostExecute(T4List<TTask> tasks) {
		if (mListener != null && !isCancelled()) {
			mListener.onGetTasksCompletation(tasks, mException);
		}
	}
		
	public interface IGetTasksTaskCompletation {
		public void onGetTasksCompletation(T4List<TTask> tasks, T4Exception ex);
	}
}
