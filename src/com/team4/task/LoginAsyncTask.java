package com.team4.task;

import android.content.Context;
import android.os.AsyncTask;

import com.team4.manager.HttpManager;
import com.team4.type.TStudent;
import com.team4.utils.exceptions.T4Exception;

public class LoginAsyncTask extends AsyncTask<String, Void, TStudent>{
	
	private T4Exception mException;
	private String mUserName;
	private String mPassword;
	private Context mContext;
	
	public LoginAsyncTask(Context context) {
		mContext = context;
	}
	
	@Override
	public TStudent doInBackground(String... params) {
		mUserName = params[0];
		mPassword = params[1];
		
		TStudent student = null;
	
		try {
			student = HttpManager.instance().login(mContext, mUserName, mPassword);
		} catch (T4Exception ex) {
			mException = ex;
		} 
	
		return student;
	}
	
	@Override
	public void onPostExecute(TStudent student) {
		if (mContext != null 
				&& mContext instanceof ILoginTaskCompletation
				&& !isCancelled()) {
			ILoginTaskCompletation listener = (ILoginTaskCompletation)mContext;
			listener.onLoginCompletation(student, mException);
		}
	}
		
	public interface ILoginTaskCompletation {
		public void onLoginCompletation(TStudent student, T4Exception ex);
	}
}
