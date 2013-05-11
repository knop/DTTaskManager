package com.team4.activities;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.team4.consts.T4Function;
import com.team4.dttaskmanager.R;

public class MediaPalyDialog extends Dialog implements 
	OnClickListener, OnCompletionListener, OnErrorListener, OnPreparedListener, OnInfoListener{

	private final int fInterval = 5;
	
	private MediaPlayer mMediaPlayer;
	private String mUrl;
	private View mViewWaiting;
	private View mViewPlaying;
	private SeekBar mSeekBar;
	private TextView mTvProgress;
	private ImageButton mBtnPlay;
	private ImageButton mBtnPause;
	private boolean mIsPause;

	private UpdateUIHandler mHandler = new UpdateUIHandler(this);
    
	public MediaPalyDialog(Context context, String url) {
		super(context);
		mUrl = url;
		mIsPause = false;
		createMediaPlayer();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_play_dialog);
        mViewWaiting = findViewById(R.id.pb_waiting);
        mViewPlaying = findViewById(R.id.iv_play_icon);
        mSeekBar = (SeekBar)findViewById(R.id.sb_progress);
        mTvProgress = (TextView)findViewById(R.id.tv_progress);
        
    	mViewWaiting.setVisibility(View.GONE);
    	mViewPlaying.setVisibility(View.VISIBLE);

    	mBtnPlay = (ImageButton)findViewById(R.id.btn_play);
    	mBtnPause = (ImageButton)findViewById(R.id.btn_pause);
    	mBtnPlay.setOnClickListener(this);
    	mBtnPause.setOnClickListener(this);
        findViewById(R.id.btn_previous).setOnClickListener(this);
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
		if (mHandler != null) {
			mHandler.removeMessages(0);
		}
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
		case R.id.btn_play:
			onPlayMedia();
			break;
		case R.id.btn_pause:
			onPauseMedia();
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

	private void onPlayMedia() {
		if (mMediaPlayer == null) {	
			createMediaPlayer();
			resetMediaPlayer(); 
		}
		
		if (!mMediaPlayer.isPlaying()) {
			if (mIsPause) {
				mBtnPlay.setEnabled(false);
				mBtnPause.setEnabled(true);
				mMediaPlayer.start();
			} else {
				mBtnPlay.setEnabled(false);
				mBtnPause.setEnabled(false);
		    	mViewWaiting.setVisibility(View.VISIBLE);
		    	mViewPlaying.setVisibility(View.GONE);
				try {
					mMediaPlayer.prepareAsync();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
			mIsPause = false;
		}	
	}
	
	private void onStopMedia() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			resetMediaPlayer();
		}
	}
	
	private void onPauseMedia() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mIsPause = true;
			mBtnPlay.setEnabled(true);
			mBtnPause.setEnabled(false);
			mMediaPlayer.pause();
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
		mp.seekTo(0);
		mIsPause = true;
		mBtnPlay.setEnabled(true);
		mBtnPause.setEnabled(false);
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
    	mViewWaiting.setVisibility(View.GONE);
    	mViewPlaying.setVisibility(View.VISIBLE);
    	mTvProgress.setVisibility(View.VISIBLE);
		mBtnPlay.setEnabled(false);
		mBtnPause.setEnabled(true);
    	if (mMediaPlayer != null)
    		mMediaPlayer.start();
    	if (mHandler != null)
    		mHandler.sendMessage(mHandler.obtainMessage(UpdateUIHandler.fMsgUpdateProgressView));
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}
	
    private void updateProgressView() {
    	if (mMediaPlayer != null) {
			int position = mMediaPlayer.getCurrentPosition();		
			int mMax = mMediaPlayer.getDuration();
			int sMax = mSeekBar.getMax();
			mTvProgress.setText("时间："+T4Function.timeFormat(position)+"/"+T4Function.timeFormat(mMax));
			mSeekBar.setProgress(position*sMax/mMax);   
    	}
    }
	
	private static class UpdateUIHandler extends Handler {
		
		private static final int fMsgUpdateProgressView = 0;
		
		private final int fUpdateInterval = 100;
		
		WeakReference<MediaPalyDialog> mDialog;
		
		UpdateUIHandler(MediaPalyDialog dialog) {
			mDialog = new WeakReference<MediaPalyDialog>(dialog);
		}
		
        public void handleMessage(Message msg) {  
        	mDialog.get().updateProgressView();
        	sendMessageDelayed(obtainMessage(fMsgUpdateProgressView), fUpdateInterval);
        }  
	}
}
