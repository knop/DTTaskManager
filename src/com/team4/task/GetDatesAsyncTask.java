package com.team4.task;

import android.content.Context;
import android.os.AsyncTask;

import com.team4.manager.HttpManager;
import com.team4.type.TDate;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.type.T4List;

public class GetDatesAsyncTask extends AsyncTask<String, Void, T4List<TDate>>{
	
	private T4Exception mException;
	private Context mContext;
	
	public GetDatesAsyncTask(Context context) {
		mContext = context;
	}
	
	@Override
	public T4List<TDate> doInBackground(String... params) {
		String studentId = params[0];
		
		T4List<TDate> dates = null;
	
		try {
			dates = HttpManager.instance().getDateList(mContext, studentId);
		} catch (T4Exception ex) {
			mException = ex;
		} 
	
		return dates;
	}
	
	@Override
	public void onPostExecute(T4List<TDate> dates) {
		if (mContext != null 
				&& mContext instanceof IGetDatesTaskCompletation
				&& !isCancelled()) {
			IGetDatesTaskCompletation listener = (IGetDatesTaskCompletation)mContext;
			listener.onGetDatesCompletation(dates, mException);
		}
	}
		
	public interface IGetDatesTaskCompletation {
		public void onGetDatesCompletation(T4List<TDate> dates, T4Exception ex);
	}
}
