package com.team4.type;

import java.io.Serializable;

import com.team4.utils.type.IBaseType;

public class TTask implements IBaseType, Serializable{
	
	private static final long serialVersionUID = -2633427941274261054L;
	
	private String mJob;
	private String mAttachment;
	
	public String getJob() {
		return mJob;
	}
	
	public void setJob(String job) {
		this.mJob = job;
	}
	
	public String getAttachment() {
		return mAttachment;
	}
	
	public void setAttachment(String attachment) {
		this.mAttachment = attachment;
	}
	
	
}