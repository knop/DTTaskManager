package com.team4.activities;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.team4.consts.T4Function;
import com.team4.dttaskmanager.R;
import com.team4.exceptions.ErrorCode;
import com.team4.manager.HttpManager;
import com.team4.task.DownloadAsyncTask;
import com.team4.task.DownloadAsyncTask.OnDownloadListener;
import com.team4.utils.util.T4Log;

public class DownloadDialog extends ProgressDialog implements OnDownloadListener {

	private String mRelativeUrl;
	private DownloadAsyncTask mTask;

	public DownloadDialog(Context context, String relativeUrl) {
		super(context);
		mRelativeUrl = relativeUrl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void show() {
		String path = T4Function.rootFolderPath()
				+ T4Function.getFileNameFromPath(mRelativeUrl);
		T4Log.d("path = " + path);
		mTask = HttpManager.instance().downloadFile(mRelativeUrl, path, this);
		setMessage("正在下载文件……");
		super.show();
	}

	@Override
	public void dismiss() {
		mTask.cancel(true);
		super.dismiss();
	}

	@Override
	public void onDownloadEnd(int result, String filePath) {
		// TODO Auto-generated method stub
		if (result == ErrorCode.DOWNLOAD_OK) {
			openFile(filePath);
		} else {
			Toast.makeText(this.getContext(), "下载失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void openFile(String filePath) {
		if (filePath != null
				&& T4Function.checkFileTypeInStringArray(filePath, getContext()
						.getResources().getStringArray(R.array.txt))) {
			File file = new File(filePath);
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			String type = T4Function.getMIMEType(file);
			intent.setDataAndType(Uri.fromFile(file), type);
			getContext().startActivity(intent);
			dismiss();
		} else {
			Toast.makeText(getContext(), "无法打开，请安装相应的软件！", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
