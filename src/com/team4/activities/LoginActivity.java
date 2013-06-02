package com.team4.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team4.consts.T4Const;
import com.team4.consts.T4Function;
import com.team4.dttaskmanager.R;
import com.team4.manager.HttpManager;
import com.team4.task.GetDatesAsyncTask;
import com.team4.task.GetDatesAsyncTask.IGetDatesTaskCompletation;
import com.team4.task.LoginAsyncTask;
import com.team4.task.LoginAsyncTask.ILoginTaskCompletation;
import com.team4.type.TDate;
import com.team4.type.TStudent;
import com.team4.utils.exceptions.T4Exception;
import com.team4.utils.type.T4List;

import java.io.IOException;

public class LoginActivity extends Activity implements 
	OnClickListener, ILoginTaskCompletation, IGetDatesTaskCompletation{
	
	private EditText mETUserName;
	private EditText mETPassword;
	private ProgressDialog mPDWaiting;
	
	private TStudent mStudent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setHost();

        mETUserName = (EditText)findViewById(R.id.et_username);
        mETPassword = (EditText)findViewById(R.id.et_password);
        
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();
		switch (id) {
		case R.id.btn_login:
			this.login();
			break;
		case R.id.btn_exit:
			this.exit(); 
			break;
        case R.id.tv_host:
            this.setHost();
            break;
		default:
			break;
		}
	}

    private void setHost() {
        try {
            HttpManager.instance().setHost(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView tvHost = (TextView)findViewById(R.id.tv_host);
        tvHost.setOnClickListener(this);
        tvHost.setText("Host:" + HttpManager.instance().getHost());
    }

	private void login() {
		String userName = mETUserName.getText().toString();
		String password = mETPassword.getText().toString();
		if ((userName.length() > 0) && (password.length() > 0)) {
			mPDWaiting = ProgressDialog.show(this, "筑梦堂", "登录中，请稍候……");
			LoginAsyncTask task = new LoginAsyncTask(this);
			task.execute(userName, password);
		} else {	
			Toast.makeText(this, R.string.login_invalid, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void exit() {
		System.exit(0);
	}

	@Override
	public void onLoginCompletation(TStudent student, T4Exception ex) {		
		if (student != null) {
			mStudent = student;
			GetDatesAsyncTask task = new GetDatesAsyncTask(this);
			task.execute(String.valueOf(student.getId()));
		} else {
			mPDWaiting.dismiss();
			String errorMessage;
			if (ex != null)
				errorMessage = ex.getMessage();
			else
				errorMessage = getResources().getString(R.string.login_error);
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetDatesCompletation(T4List<TDate> dates, T4Exception ex) {
		mPDWaiting.dismiss();
		if (dates != null) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(T4Const.kDates, dates);
			intent.putExtra(T4Const.kStudent, mStudent);
			this.startActivity(intent);	
			this.finish();
		} else {
			String errorMessage;
			if (ex != null)
				errorMessage = ex.getMessage();
			else
				errorMessage = getResources().getString(R.string.login_error);
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();			
		}	
	}
}
