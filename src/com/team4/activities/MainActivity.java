package com.team4.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.team4.consts.T4Const;
import com.team4.dttaskmanager.R;
import com.team4.task.GetDatesAsyncTask;
import com.team4.task.GetDatesAsyncTask.IGetDatesTaskCompletation;
import com.team4.type.TDate;
import com.team4.type.TStudent;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.type.T4List;

public class MainActivity extends Activity implements
		LeftListFragment.Callbacks, OnClickListener, IGetDatesTaskCompletation {

	private View mViewEmpty;
	private View mViewContainer;
	private ArrayList<TDate> mDates;
	private TStudent mStudent;
	private LeftListFragment mLeftFragment;
	private RightDetailFragment mRightFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewEmpty = findViewById(R.id.right_detail_empty);
		mViewContainer = findViewById(R.id.item_detail_container);
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		this.setupLeftFragment();
	}

	@SuppressWarnings("unchecked")
	private void setupLeftFragment() {
		Intent intent = getIntent();
		mDates = (ArrayList<TDate>) intent.getSerializableExtra(T4Const.kDates);
		mStudent = (TStudent) intent.getSerializableExtra(T4Const.kStudent);
		TextView tvStudentInfo = (TextView)mViewEmpty.findViewById(R.id.tv_student_info);
		if (mStudent != null && mDates != null) {
            tvStudentInfo.setText(mStudent.getSchool()+" "+mStudent.getName());
			mLeftFragment = (LeftListFragment) getFragmentManager()
					.findFragmentById(R.id.left_list_fragment);
			TDate totalDate = new TDate();
			totalDate.setDate("总览");
			mDates.add(0, totalDate);
			mLeftFragment.setListAdapter(new ArrayAdapter<TDate>(this,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, mDates));
			mLeftFragment.setActivateOnItemClick(true);
		}
	}

	@Override
	public void onItemSelected(int pos) {
		if (mDates != null && mDates.size() > 0 && pos < mDates.size()) {
			mViewEmpty.setVisibility(View.GONE);
			mViewContainer.setVisibility(View.VISIBLE);
			Bundle arguments = new Bundle();
			TDate date = mDates.get(pos);
			arguments.putString(T4Const.kDate, date.getDate());
			arguments.putString(T4Const.kStudentId, String.valueOf(mStudent.getId()));
			mRightFragment = new RightDetailFragment();
			mRightFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, mRightFragment).commit();
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();
		switch (id) {
		case R.id.btn_logout:
			onLogout();
			break;
		case R.id.btn_refresh:
			onRefresh();
			break;
		}
	}

	@Override
	public void onBackPressed() {		
		new AlertDialog.Builder(this).setTitle("筑梦堂")
				.setMessage("确定要退出程序吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						System.exit(0);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						;
					}
				})
				.show();
	}

	private void onRefresh() {
		GetDatesAsyncTask task = new GetDatesAsyncTask(this);
		String studentId = String.valueOf(mStudent.getId());
		task.execute(studentId);
	}

	private void onLogout() {
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	@Override
	public void onGetDatesCompletation(T4List<TDate> dates, T4Exception ex) {
		if (dates != null) {
			TDate totalDate = new TDate();
			totalDate.setDate("总览");
			dates.add(0, totalDate);
			mLeftFragment.setListAdapter(new ArrayAdapter<TDate>(this,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, dates));
			mViewEmpty.setVisibility(View.VISIBLE);
			mViewContainer.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().remove(mRightFragment).commit();
		} else {
			String errorMessage;
			if (ex != null)
				errorMessage = ex.getMessage();
			else
				errorMessage = getResources().getString(R.string.login_error);
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
		// TODO Auto-generated method stub

	}
}
