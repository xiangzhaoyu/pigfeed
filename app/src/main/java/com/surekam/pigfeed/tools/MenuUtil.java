package com.surekam.pigfeed.tools;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.surekam.pigfeed.R;

public class MenuUtil {

	public static void setMenuBack(final Activity context, View v) {
		setMenuBack(context, v, true);
	}

	public static void setMenuBack(final Activity context, View v,
			boolean visible) {
		ImageView back = (ImageView) v.findViewById(R.id.topbar_left_btn);

		if (back != null) {
			if (visible) {
				back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						context.onBackPressed();
					}
				});
			} else {
				back.setVisibility(View.INVISIBLE);
			}
		}
	}

	public static void setMenuMore(final Activity context, View v, int ResId,
			OnClickListener listner) {
		setMenuMore(context, v, true, ResId, listner);
	}

	public static void setMenuMore(final Activity context, View v,
			boolean visible) {
		setMenuMore(context, v, visible, 0, null);
	}

	public static void setMenuMore(final Activity context, View v,
			boolean visible, int ResId, OnClickListener listner) {
		ImageView back = (ImageView) v.findViewById(R.id.topbar_right_image);

		v.findViewById(R.id.topbar_right_image).setVisibility(View.INVISIBLE);
		v.findViewById(R.id.topbar_right_btn).setVisibility(View.INVISIBLE);

		if (back != null) {
			if (visible) {
				if (ResId > 0) {
					back.setVisibility(View.VISIBLE);
					back.setImageResource(ResId);
				}
				back.setOnClickListener(listner);
			} else {
				back.setVisibility(View.INVISIBLE);
			}
		}

	}

	public static void setMenuMore(final Activity context, View v,
			boolean visible, String text, OnClickListener listner) {
		Button back = (Button) v.findViewById(R.id.topbar_right_btn);

		v.findViewById(R.id.topbar_right_image).setVisibility(View.INVISIBLE);
		v.findViewById(R.id.topbar_right_btn).setVisibility(View.INVISIBLE);

		if (back != null) {
			if (visible) {
				if (!StringUtils.isEmpty(text)) {
					back.setVisibility(View.VISIBLE);
					back.setText(text);
					back.setOnClickListener(listner);
				}

			} else {
				back.setVisibility(View.INVISIBLE);
			}
		}

	}

	public static void setMenuTitle(View v, String title) {
		TextView tv = (TextView) v.findViewById(R.id.topbar_title);
		if (tv != null) {
			tv.setText(title);
		}
	}

	public static void setMenuBackTransparent(View v) {
		v.findViewById(R.id.topbar_layout).setBackgroundResource(
				R.color.transparent);

	}

}
