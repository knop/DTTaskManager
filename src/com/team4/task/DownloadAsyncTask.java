package com.team4.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.team4.exceptions.ErrorCode;
import com.team4.utils.util.T4Log;

import android.os.AsyncTask;

public class DownloadAsyncTask extends AsyncTask<String, Void, Integer> {
	private OnDownloadListener mDownloadListener;
	private String mSavePath;
	
	public interface OnDownloadListener {
		public void onDownloadEnd(int result, String filePath);	
	}
	
	public DownloadAsyncTask(OnDownloadListener l){
		mDownloadListener = l;
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		int result = ErrorCode.DOWNLOAD_OK;
		String url = arg0[0];
		mSavePath = arg0[1];
		RandomAccessFile oSavedFile = null;
		InputStream in = null;
		HttpURLConnection mhuc = null;
		
		try {
			String encodeURL = URLEncoder.encode(url, "UTF-8");
			encodeURL = encodeURL.replaceAll("\\+", "%20");// 处理空格
			encodeURL = encodeURL.replaceAll("%3A", ":");// 处理:
			encodeURL = encodeURL.replaceAll("%2F", "/");// 处理/

			URL downloadUrl = new URL(encodeURL);
			mhuc = (HttpURLConnection) downloadUrl.openConnection();
			mhuc.setConnectTimeout(10 * 1000);
			mhuc.setReadTimeout(10 * 1000);

			String tempFilePath = getDownloadTmpPath(mSavePath);
			File file = new File(tempFilePath);
			if(file.exists())
				file.delete();
			file.createNewFile();
			
			oSavedFile = new RandomAccessFile(file, "rw");
			int DownloadBuffSize = 10 * 1024;
			byte[] bytes = new byte[DownloadBuffSize];

			in = mhuc.getInputStream();
			int c = 0;
			while (!isCancelled() && null != in
					&& (c = in.read(bytes)) != -1) {
				T4Log.d("bytes="+bytes);
				oSavedFile.write(bytes, 0, c);
			}

			if (!isCancelled()) {
				in.close();
				in = null;
				oSavedFile.close();
				oSavedFile = null;
				File newFile = new File(mSavePath);
				if(!newFile.exists()){					
					file.renameTo(newFile);
				}
				result = ErrorCode.DOWNLOAD_OK;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result = ErrorCode.DOWNLOAD_FAILED;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			if (null != oSavedFile) {
				try {
					oSavedFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				oSavedFile = null;
			}

			if (null != mhuc) {
				mhuc.disconnect();
			}
			mhuc = null;

			try {
				finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		return result;
	}
	
	private String getDownloadTmpPath(String path){		
		return path + "_TMP_";
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if(mDownloadListener != null && mSavePath != null)
			mDownloadListener.onDownloadEnd(result,mSavePath);
	}
	
	@Override
	protected void onCancelled() {
		if(mDownloadListener != null)
			mDownloadListener.onDownloadEnd(ErrorCode.DOWNLOAD_CANCEL, null);		
	}
}
