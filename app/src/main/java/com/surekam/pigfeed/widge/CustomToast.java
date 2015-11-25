package com.surekam.pigfeed.widge;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.surekam.pigfeed.R;
/**
 * 自定义带图标的toast
 * @author liukai
 *
 */
public class CustomToast {
	static Toast mToast;
	
	static int gravity;
	static int xpos;
	static int ypos;
	

	public static void showCustomToast(Context context,String text,int showTimes){
		gravityCenter();
		showToast(context, 
				context.getResources().getDrawable(R.drawable.item_info_normal),
				text,
				showTimes
				);
	}
	
	
	public static void showCustomToastTop(Context context,String text,int showTimes){
		gravityTop();
		showToast(context, 
				context.getResources().getDrawable(R.drawable.item_info_normal),
				text,
				showTimes
				);
	}
	
	
	private static void gravityCenter(){
		gravity =Gravity.CENTER;
		xpos = 0;
		ypos = 0;	
	}
	
	private static void gravityTop(){
		gravity =Gravity.TOP;
		xpos = 0;
		ypos = 0;	
	}
	
	
	/*
	 * 从布局文件中加载布局并且自定义显示Toast
	 */
	private static  void showToast(Context context,Drawable res,
			String text ,int showTime ){
		//获取LayoutInflater对象，该对象可以将布局文件转换成与之一致的view对象
		LayoutInflater inflater=LayoutInflater.from(context);
		//将布局文件转换成相应的View对象
		View layout=inflater.inflate(R.layout.custome_progress_toast_layout,null);
				//(ViewGroup) findViewById(R.id.toast_layout_root));
		//从layout中按照id查找imageView对象
		ImageView imageView=(ImageView)layout.findViewById(R.id.toast_pic);
		//设置ImageView的图片
		imageView.setBackgroundDrawable(res);
		//从layout中按照id查找TextView对象
		
		TextView textView = (TextView) layout.findViewById(R.id.toast_text);
		//设置TextView的text内容
		textView.setText(text);
		//实例化一个Toast对象
		if (mToast ==null){
			mToast=new Toast(context);
			mToast.setDuration(showTime);
			mToast.setGravity(Gravity.BOTTOM, 0, 80);
			mToast.setView(layout);
			mToast.show();	
		}else{
			mToast.setView(layout);
			mToast.show();
		}

	}

}
