package com.team4.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import com.team4.consts.T4Const;
import com.team4.dttaskmanager.R;
import com.team4.type.TDate;

public class MainActivity extends FragmentActivity implements
		LeftListFragment.Callbacks {
	
	private View mViewEmpty;
	private View mViewContainer;
	private ArrayList<TDate> mDates;
	private String mStudentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewEmpty = findViewById(R.id.right_detail_empty);
		mViewContainer = findViewById(R.id.item_detail_container);
		this.setupLeftFragment();
	}
	
	@SuppressWarnings("unchecked")	
	private void setupLeftFragment() {
		Intent intent = getIntent();
		mDates = (ArrayList<TDate>)intent.getSerializableExtra(T4Const.kDates);
		mStudentId = intent.getStringExtra(T4Const.kStudentId);
		if (mDates != null && mDates.size() > 0) {
			LeftListFragment fragment = (LeftListFragment)getSupportFragmentManager().
					findFragmentById(R.id.left_list_fragment);
			fragment.setListAdapter(new ArrayAdapter<TDate>(this,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, mDates));
			fragment.setActivateOnItemClick(true);	
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
			arguments.putString(T4Const.kStudentId, mStudentId);
			RightDetailFragment fragment = new RightDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();
		}
	}
}
