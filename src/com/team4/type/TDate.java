package com.team4.type;

import java.io.Serializable;

import com.team4.utils.type.IBaseType;

public class TDate implements IBaseType, Serializable{

	private static final long serialVersionUID = -4549154960658266024L;
	
	private String mDate;

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}
	
	@Override
	public String toString() {
		if (mDate != null)
			return mDate;
		return "";
	}   
}