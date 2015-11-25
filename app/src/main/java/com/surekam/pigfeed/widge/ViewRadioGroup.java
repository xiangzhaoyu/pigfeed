package com.surekam.pigfeed.widge;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.tools.DebugUtil;

public class ViewRadioGroup extends LinearLayout {

	public static final int FIRST = 1;
	public static final int SECEND = 2;
	public static final int THIRD = 3;
	public static final int FOURTH = 4;
	public static final int FIVE = 5;

	Context _context;
	RadioGroup _radiogroup;
	List<RadioButton> _listRadioButton;

	onSelectedListener _listener;

	public interface onSelectedListener {
		public void onSelectedChanged(int whitch);
	}

	public ViewRadioGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public ViewRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	void initView(Context context) {
		_context = context;

		LayoutInflater.from(_context).inflate(R.layout.view_radio_group, this);

		_radiogroup = (RadioGroup) findViewById(R.id.rg_group);
		_radiogroup.setOnCheckedChangeListener(mCheckChangeListner);

		_listRadioButton = new ArrayList<RadioButton>();
		_listRadioButton.add((RadioButton) findViewById(R.id.rb_1));
		_listRadioButton.add((RadioButton) findViewById(R.id.rb_2));
		_listRadioButton.add((RadioButton) findViewById(R.id.rb_3));
		_listRadioButton.add((RadioButton) findViewById(R.id.rb_4));
		_listRadioButton.add((RadioButton) findViewById(R.id.rb_5));
	}

	public void setSelectChangedListener(onSelectedListener listner) {
		_listener = listner;
	}

	public void setDrawableLeft(int whitch, int resId) {
		Drawable pic = getResources().getDrawable(resId);

		if (pic != null) {
			switch (whitch) {
			case 1:
				((RadioButton) findViewById(R.id.rb_1)).setCompoundDrawables(
						pic, null, null, null);
				break;
			case 2:
				((RadioButton) findViewById(R.id.rb_2)).setCompoundDrawables(
						pic, null, null, null);
				break;
			case 3:
				((RadioButton) findViewById(R.id.rb_3)).setCompoundDrawables(
						pic, null, null, null);
				break;
			case 4:
				((RadioButton) findViewById(R.id.rb_4)).setCompoundDrawables(
						pic, null, null, null);
				break;
			case 5:
				((RadioButton) findViewById(R.id.rb_5)).setCompoundDrawables(
						pic, null, null, null);
				break;
			default:
				break;
			}
		}

	}

	public void setBackGroud(int whitch, int resId) {
		switch (whitch) {
		case 1:
			((RadioButton) findViewById(R.id.rb_1))
					.setBackgroundResource(resId);
			break;
		case 2:
			((RadioButton) findViewById(R.id.rb_2))
					.setBackgroundResource(resId);
			break;
		case 3:
			((RadioButton) findViewById(R.id.rb_3))
					.setBackgroundResource(resId);
			break;
		case 4:
			((RadioButton) findViewById(R.id.rb_4))
					.setBackgroundResource(resId);
			break;
		case 5:
			((RadioButton) findViewById(R.id.rb_5))
					.setBackgroundResource(resId);
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @param index
	 *            1-4
	 */
	public void setIndex(int index) {
		switch (index + 1) {
		case FIRST:
			((RadioButton) findViewById(R.id.rb_1)).setChecked(true);
			break;
		case SECEND:
			((RadioButton) findViewById(R.id.rb_2)).setChecked(true);
			break;
		case THIRD:
			((RadioButton) findViewById(R.id.rb_3)).setChecked(true);
			break;
		case FOURTH:
			((RadioButton) findViewById(R.id.rb_4)).setChecked(true);
			break;
		case FIVE:
			((RadioButton) findViewById(R.id.rb_5)).setChecked(true);
			break;

		default:
			break;
		}
	}

	public void setRadioButtonText(String... titles) {
		int i = 0;
		int count = titles.length;
		for (RadioButton rb : _listRadioButton) {
			if (count > i) {
				rb.setVisibility(View.VISIBLE);
				rb.setText(titles[i]);
				i++;
			} else {
				rb.setVisibility(View.GONE);
			}
		}
	}

	private OnCheckedChangeListener mCheckChangeListner = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			DebugUtil.printDebugTag("ViewRadioGroup", "选中了按钮" + arg1);
			int whitch = 0;
			switch (arg1) {
			case R.id.rb_1:
				whitch = FIRST;
				break;
			case R.id.rb_2:
				whitch = SECEND;
				break;
			case R.id.rb_3:
				whitch = THIRD;
				break;
			case R.id.rb_4:
				whitch = FOURTH;
				break;
			case R.id.rb_5:
				whitch = FIVE;
				break;
			default:
				break;
			}

			if (whitch > 0) {
				if (_listener != null)
					_listener.onSelectedChanged(whitch);
			}

		}
	};

}
