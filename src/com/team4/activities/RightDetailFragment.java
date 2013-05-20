package com.team4.activities;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.team4.consts.T4Const;
import com.team4.consts.T4Function;
import com.team4.dttaskmanager.R;
import com.team4.manager.HttpManager;
import com.team4.task.GetTasksAsyncTask;
import com.team4.task.GetTasksAsyncTask.IGetTasksTaskCompletation;
import com.team4.type.TTask;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.type.T4List;
import com.team4.utils.util.T4Log;

public class RightDetailFragment extends Fragment implements
	IGetTasksTaskCompletation, OnItemClickListener{
	
	private String mDate;
	private String mStudentId;
	private View mViewWaiting;
	private View mViewRetry;
	private GridView mGVContent;
	
	public RightDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(T4Const.kDate)) {
			mDate = getArguments().getString(T4Const.kDate);
		}
		
		if (getArguments().containsKey(T4Const.kStudentId)) {
			mStudentId = getArguments().getString(T4Const.kStudentId);
		}
	} 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_right_detail, container, false);
		View view = rootView.findViewById(R.id.right_detail_imageview);
		mViewWaiting = rootView.findViewById(R.id.right_detail_waiting);
		mViewRetry = rootView.findViewById(R.id.right_detail_retry);
		mGVContent = (GridView)rootView.findViewById(R.id.right_detail_gridview);
		if(mDate.equalsIgnoreCase("总览")) {			
			view.setVisibility(View.VISIBLE);
			mViewWaiting.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.GONE);
			mGVContent.setOnItemClickListener(this);
			mViewWaiting.setVisibility(View.VISIBLE);
			GetTasksAsyncTask task = new GetTasksAsyncTask(this.getActivity(), this);
			task.execute(mStudentId, mDate);
		}
		return rootView;
	}

	@Override
	public void onGetTasksCompletation(T4List<TTask> tasks, T4Exception ex) {
		mViewWaiting.setVisibility(View.GONE);
		if (tasks != null) {
			mGVContent.setVisibility(View.VISIBLE);
			TasksAdapter adapter = new TasksAdapter(getActivity(), tasks);
			mGVContent.setAdapter(adapter);
			mViewRetry.setVisibility(View.GONE);
		} else {
			mGVContent.setVisibility(View.GONE);
			mViewRetry.setVisibility(View.VISIBLE);
			mViewRetry.findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mViewRetry.setVisibility(View.GONE);
					mViewWaiting.setVisibility(View.VISIBLE);
				}
				
			});
		}	
	}
	
	private static class TasksAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		private T4List<TTask> mTasks;
		
		public TasksAdapter(Context context, T4List<TTask> tasks) {
			super();
			mTasks = tasks;
			if (context != null)
				mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			if (mTasks == null)
				return 0;
			return mTasks.size();
		}

		@Override
		public TTask getItem(int pos) {
			if (mTasks == null)
				return null;
			return mTasks.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;
			TTask task = getItem(pos);
			if (convertView == null) {
				view = newItemView(null);
			} else {
				view = convertView;
			}
			
			bindView(view, task);
			
			return view;
		}
		
		private void bindView(View view, TTask task) {
			if (view == null || task == null)
				return;
			
			ViewHolder holder = (ViewHolder)view.getTag();
			holder.tvName.setText(task.getJob());
			holder.task = task;
		}
		
		private View newItemView(ViewGroup parent) {
			if (mInflater == null)
				return null;
			ViewHolder holder = new ViewHolder();
			View view = mInflater.inflate(R.layout.view_task_item, parent);
			holder.tvName = (TextView)view.findViewById(R.id.tv_task_name);
			view.setTag(holder);
			return view;			
		}
		
		public static class ViewHolder {
			public TextView tvName;
			public TTask task;
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TasksAdapter.ViewHolder holder = (TasksAdapter.ViewHolder)view.getTag();
		T4Log.d(holder.task.getJob());
		String job = holder.task.getJob();
		String url = holder.task.getAttachment();
		if (T4Function.checkFileTypeInStringArray(url, getResources().getStringArray(R.array.audio))) {
			MediaPalyDialog dialog = new MediaPalyDialog(getActivity(), HttpManager.fillUrl(url));
			dialog.setTitle(job+" - "+T4Function.getFileNameFromPath(url));
			dialog.show();
		} else if (T4Function.checkFileTypeInStringArray(url, getResources().getStringArray(R.array.txt))) {
			DownloadDialog dialog = new DownloadDialog(getActivity(), url);
			dialog.setTitle(job);
			dialog.show();			
		} else {
			T4Log.d("文件类型暂不支持！");
		}
	}
}
