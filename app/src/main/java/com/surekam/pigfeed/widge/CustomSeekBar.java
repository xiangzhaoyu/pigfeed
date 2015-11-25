package com.surekam.pigfeed.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar{
	Context _context;

	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		_context = context;
		init();
	}
	
	private void init(){
		setMax(100);
		setProgress(0);
	}
	
	
//	
//	@Override
//	public void onStartTemporaryDetach() {
//		// TODO Auto-generated method stub
//		int seekProgress = getProgress();
//		if (seekProgress<=10){
//			setProgress(5);
//		}
//		if (seekProgress >10 && seekProgress<=20){
//			setProgress(15);
//		}
//		if (seekProgress >20 && seekProgress<=30){
//			setProgress(15);
//		}
//		
//		
//		
//		
//		//super.onStartTemporaryDetach();
//	}

}
