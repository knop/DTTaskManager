package com.team4.manager;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class MediaManager implements OnCompletionListener, OnPreparedListener{
	
	private static MediaManager sInstance;
	
	private MediaPlayer mMediaPlayer;
	private IMediaListener mListener;
	
	private MediaManager() {
		this.createMediaPlayer();
	}
	
	private void createMediaPlayer() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
		}
	}
	
	public static MediaManager instance() {
		if (sInstance == null) {
			sInstance = new MediaManager();
		} 
		
		return sInstance;
	}
	
	public void setListener(IMediaListener listener) {
		mListener = listener;
	}
	
	public void play(String url) {		
		this.createMediaPlayer();
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mMediaPlayer.setDataSource(url);
				if (mListener != null)
					mListener.onPrepare();
				mMediaPlayer.prepare(); // might take long! (for buffering, etc)
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mMediaPlayer.start();
		}
	}
	
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	public void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
		}
	}
	
	public void previous(int second) {
		if (mMediaPlayer != null) {
			int pos = mMediaPlayer.getCurrentPosition();
			pos -= second * 1000;
			mMediaPlayer.seekTo(pos);
		}
	}
	
	public void next(int second) {
		if (mMediaPlayer != null) {
			int pos = mMediaPlayer.getCurrentPosition();
			pos += second * 1000;
			mMediaPlayer.seekTo(pos);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mListener != null)
			mListener.onStop();		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (mListener != null)
			mListener.onPrepare();			
	}	
		
	public interface IMediaListener {
		public void onPrepare();
		public void onPlay();
		public void onPause();
		public void onStop();
		public void onProgress(int pos);
	}
}
