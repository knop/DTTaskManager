package com.team4.activities;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.team4.dttaskmanager.R;

public class MediaPalyDialog extends Dialog implements 
	OnClickListener, OnCompletionListener, OnErrorListener, OnPreparedListener, OnInfoListener{

	private final int fInterval = 5;
	
	private MediaPlayer mMediaPlayer;
	private String mUrl;
	private View mViewWaiting;
	private View mViewPlaying;
	private Button mBtnPlayAndPause;
	
	public MediaPalyDialog(Context context, String url) {
		super(context);
		mUrl = url;
		createMediaPlayer();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_play_dialog);
        mViewWaiting = findViewById(R.id.pb_waiting);
        mViewPlaying = findViewById(R.id.iv_play_icon);
    	mViewWaiting.setVisibility(View.GONE);
    	mViewPlaying.setVisibility(View.VISIBLE);
        mBtnPlayAndPause = (Button)findViewById(R.id.btn_play_and_pause);
        mBtnPlayAndPause.setOnClickListener(this);
        findViewById(R.id.btn_previous).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }
    
    @Override
    public void show() {
    	resetMediaPlayer();
    	super.show();
    }    
    
    @Override
    public void dismiss() {
    	onStopMedia();
    	if (mMediaPlayer != null) {
    		mMediaPlayer.release();
    		mMediaPlayer = null;
    	}
    	super.dismiss();
    }

    private void createMediaPlayer() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setWakeMode(this.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);     	
    }
    
    private void resetMediaPlayer() {
    	if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(mUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch(id) {
		case R.id.btn_previous:
			onPrevious();
			break;
		case R.id.btn_play_and_pause:
			onPlayAnPause();
			break;
		case R.id.btn_stop:
			onStopMedia();
			break;
		case R.id.btn_next:
			onNext();
			break;
		}
	}
	
	private void onPrevious() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			int pos = mMediaPlayer.getCurrentPosition();
			pos -= fInterval * 1000;
			mMediaPlayer.seekTo(pos);
		}		
	}

	private void onPlayAnPause() {
		if (mMediaPlayer == null) {	
			createMediaPlayer();
			resetMediaPlayer();
		}
		
		if (!mMediaPlayer.isPlaying()) {
			mBtnPlayAndPause.setEnabled(false);
			mBtnPlayAndPause.setText("暂停");
	    	mViewWaiting.setVisibility(View.VISIBLE);
	    	mViewPlaying.setVisibility(View.GONE);
			try {
				mMediaPlayer.prepareAsync();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		} else {
			mBtnPlayAndPause.setText("播放");
			mMediaPlayer.pause();
		}	
	}
	
	private void onStopMedia() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mBtnPlayAndPause.setText("播放");
			mMediaPlayer.stop();
			resetMediaPlayer();
		}		
	}
	
	private void onNext() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			int pos = mMediaPlayer.getCurrentPosition();
			pos += fInterval * 1000;
			mMediaPlayer.seekTo(pos);
		}		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		onStopMedia();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		resetMediaPlayer();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		mBtnPlayAndPause.setEnabled(true);
    	mViewWaiting.setVisibility(View.GONE);
    	mViewPlaying.setVisibility(View.VISIBLE);
    	mMediaPlayer.start();
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
