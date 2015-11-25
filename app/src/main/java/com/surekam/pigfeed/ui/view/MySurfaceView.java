package com.surekam.pigfeed.ui.view;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.tools.DebugUtil;

public class MySurfaceView extends SurfaceView implements Runnable, Callback {

	public interface onValueChanged {
		public void onChanged();
	}

	private SurfaceHolder mHolder; // 用于控制SurfaceView

	private Thread t; // 声明一条线程

	private boolean flag; // 线程运行的标识，用于控制线程

	private Canvas mCanvas; // 声明一张画布

	private Paint p; // 声明一支画笔
	private boolean _isDrawing = true;

	Context context;

	SparseArray<Rect> _numbers = new SparseArray<Rect>();
	SparseIntArray _checkList = new SparseIntArray();

	Bitmap bmpNormal;
	Bitmap bmpPressed;
	Bitmap bmpBack;

	private int _diameter;// 直径,宽度或者高度取小值
	private Point _centerPoint;
	private int bmpWidth;
	private int _center = 0; // 原点X
	private int _circlePadding; // 外面
	private int _circleWidth = 20; // 圆环宽度

	int _pointX = 0;
	int _pointY = 0;

	private int _colorTimeLine = getResources().getColor(R.color.blue_sky);
	private int _widthTimeLine = 10;

	onValueChanged _valueListner;

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		mHolder = getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 为SurfaceView添加状态监听
		p = new Paint(); // 创建一个画笔对象
		p.setColor(Color.BLUE); // 设置画笔的颜色为白色
		setFocusable(true); // 设置焦点

		// setZOrderOnTop(true);
		// mHolder.setFormat(PixelFormat.TRANSLUCENT);

		init();
	}

	public void setOnValueChangeed(onValueChanged listener) {
		_valueListner = listener;
	}

	SparseIntArray _savedData;
	boolean _isDay;

	public void setValue(SparseIntArray checkList, boolean isDay) {

		DebugUtil.printDebugTag("全部数据", checkList.toString());
		_savedData = checkList.clone();;
		this._isDay = isDay;
		
		SparseIntArray sp = new SparseIntArray();


		if (isDay) {
			for (int i = 0; i <= 11; i++) {
				int check = checkList.get(i);
				sp.put(i, check);
			}
		} else {
			for (int i = 12; i <= 23; i++) {
				int check = checkList.get(i);
				sp.put(i-12, check);
			}
		}

		_checkList = sp;

		DebugUtil.printDebugTag("正在编辑数据",_isDay + "|" + _checkList.toString());

	}

	public SparseIntArray getValue() {
		int add = _isDay ? 0 : 12;

		for (int i = 0; i <= 11; i++) {
			int check = _checkList.get(i);
			_savedData.put(i + add, check);
		}

		DebugUtil.printDebugTag("全部数据", _savedData.toString());

		return _savedData;
	}

	private void init() {
		_centerPoint = new Point();
		int width = dip2px(context, 36);
		bmpNormal = readBitmap(context.getResources(), R.drawable.num_bkg_gray,
				width);
		bmpPressed = readBitmap(context.getResources(),
				R.drawable.num_bkg_blue, width);

		bmpWidth = bmpNormal.getWidth();

		_circlePadding = dip2px(context, 10); // 圆环宽度
		_widthTimeLine = dip2px(context, 20); // 连接线宽度

		for (int i = 0; i <= 11; i++) {
			_checkList.put(i, 0);
		}

		// UIHelper.ToastMessage(context, new Gson().toJson(_checkList));

	}

	/**
	 * 自定义一个方法，在画布上画一个圆
	 */
	public void Draw() {
		if (!flag)
			return;
		mCanvas = mHolder.lockCanvas(); // 获得画布对象，开始对画布画画
		if (mCanvas == null)
			return;
		mCanvas.drawRGB(255, 255, 255); // 把画布填充为黑色
		// mCanvas.drawCircle(x, y, r, p); // 画一个圆
		// mCanvas.drawColor(Color.TRANSPARENT,Mode.CLEAR); //画步透明色
		_center = _diameter / 2;
		if (bmpBack == null) {
			bmpBack = readBitmap(context.getResources(), R.drawable.clock,
					_diameter);
		}
		drawBack();

		// 绘制外圆
		// drawOutCircle();

		for (int i = 0; i <= 11; i++) {
			drawConnectLine(i);
		}

		for (int i = 0; i <= 11; i++) {
			drawButton(i);
		}

		mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上
	}

	private void drawOutCircle() {

		// dip2px(context, 83); // 内圆半径
		int innerCircle = _center - _circlePadding;

		// 第一种方法绘制圆环
		// 绘制内圆
		Paint paint = new Paint();
		paint.setAntiAlias(true); // 设置画笔为无锯齿
		// paint.setColor(Color.BLACK); // 设置画笔颜色
		// paint.setStrokeWidth((float) _circleWidth); // 线宽
		paint.setStyle(Style.STROKE); // 空心效果

		// 绘制圆环
		paint.setARGB(255, 138, 43, 226);
		paint.setStrokeWidth(_circleWidth);
		paint.setAlpha(20);

		mCanvas.drawCircle(_center, _center, innerCircle, paint);
	}

	private void drawBack() {
		int padding = dip2px(context, 2);
		mCanvas.drawBitmap(bmpBack, padding, padding, null);
	}

	private void drawButton(int number) {

		int left = 0;
		int top = 0;
		bmpWidth = bmpNormal.getWidth();
		int bmpHeight = bmpNormal.getHeight();

		int c = _center - dip2px(context, 50);
		int num = number;
		if (number == 12)
			num = 0;
		int angle = 30 * (num - 3);

		int pointX = _center + (int) (c * Math.cos(angle * 3.1 / 180));
		int pointY = _center + (int) (c * Math.sin(angle * 3.1 / 180));

		// int width = dip2px(context, 48);
		// int height = dip2px(context, 48);

		left = pointX - bmpWidth / 2;
		top = pointY - bmpHeight / 2;

		Rect rect = new Rect(left, top, left + bmpWidth, top + bmpHeight);
		_numbers.append(number, rect);

		// Paint p = new Paint();
		// p.setColor(Color.BLACK); // 设置画笔颜色

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(61, 61, 61));
		paint.setTextSize(sp2px(context, 16));
		paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

		Rect bounds = new Rect();
		String str = String.valueOf(number == 0 ? 12 : number);
		paint.getTextBounds(str, 0, str.length(), bounds);
		int x = left + (bmpNormal.getWidth() - bounds.width()) / 2;
		int y = top + rect.height() / 2 + bounds.height() / 2;

		int checke = _checkList.get(num);
		if (checke == 1) {
			mCanvas.drawBitmap(bmpPressed, rect.left, rect.top, null);
		} else {
			mCanvas.drawBitmap(bmpNormal, rect.left, rect.top, null);
		}
		mCanvas.drawText(str, x, y, paint);
	}

	public void drawConnectLine(int point) {

		int check = _checkList.get(point);
		if (check == 0)
			return;

		Paint paint = new Paint();
		paint.setAntiAlias(true); // 设置画笔为无锯齿
		paint.setColor(_colorTimeLine); // 设置画笔颜色
		paint.setStrokeWidth(_widthTimeLine); // 线宽
		paint.setStyle(Style.STROKE);

		RectF oval = new RectF(); // RectF对象
		oval.left = dip2px(context, 50);// + bmpWidth; // 左边
		oval.top = dip2px(context, 50);// + bmpWidth ; // 上边
		oval.right = _diameter - dip2px(context, 50);// + bmpWidth / 2; // 右边
		oval.bottom = _diameter - dip2px(context, 50);// + bmpWidth / 2; // 下边

		point = point == 0 ? 12 : point;
		int beginAngle = (point - 3) * 30;
		if (beginAngle < 0)
			beginAngle += 360;

		// DebugUtil.printDebugTag("弧线", point + "点开始角度:" + beginAngle
		// + " |结束角度=:" + (beginAngle + 30));

		mCanvas.drawArc(oval, beginAngle, 30, false, paint); // 绘制圆弧
	}

	/**
	 * 当SurfaceView创建的时候，调用此函数
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		t = new Thread(this); // 创建一个线程对象
		flag = true; // 把线程运行的标识设置成true
		t.start(); // 启动线程
	}

	/**
	 * 当SurfaceView的视图发生改变的时候，调用此函数
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		// _diameter = (width > height) ? height
		// : width;

		_diameter = width;

		_centerPoint.x = width / 2;
		_centerPoint.y = height / 2;
	}

	/**
	 * 当SurfaceView销毁的时候，调用此函数
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false; // 把线程运行的标识设置成false
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		// _diameter = (widthMeasureSpec > heightMeasureSpec) ?
		// heightMeasureSpec
		// : widthMeasureSpec;

		_diameter = widthMeasureSpec;

		_centerPoint.x = widthMeasureSpec / 2;
		_centerPoint.y = heightMeasureSpec / 2;

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	int _lastClock = 0;

	/**
	 * 当屏幕被触摸时调用
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		_pointX = (int) event.getX(); // 获得屏幕被触摸时对应的X轴坐标
		_pointY = (int) event.getY(); // 获得屏幕被触摸时对应的Y轴坐标

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:

			MoveJudge();
			break;
		case MotionEvent.ACTION_DOWN:
			_lastClock = 0;
			break;
		case MotionEvent.ACTION_UP:

			clickJudge();
			_lastClock = 0;
			break;

		default:
			break;
		}

		return true;
	}

	private void MoveJudge() {
		boolean ibFind = false;
		for (int i = 0; i < _numbers.size(); i++) {

			int number = _numbers.keyAt(i);
			Rect r = _numbers.valueAt(i);

			if (_pointX > r.left && _pointX < r.right && _pointY > r.top
					&& _pointY < r.bottom) {
				ibFind = true;

				int clock = number;
				if (number == 0)
					clock = 12;

				if (_lastClock == clock) {
					break;
				}

				// if (Math.abs(_lastMoveNumber - number) != 1) {
				// break;
				// }

				_lastClock = clock;
				// DebugUtil.printDebugTag("judge",
				// "小时:" + number + "坐标:" + r.toString());

				checkValue(number);

				break;
			}

		}

		if (!ibFind) {
			_lastClock = 0;
		}

	}

	private void clickJudge() {
		// DebugUtil.printDebugTag("ACTION_UP", "_pointX:" + _pointX
		// + " |_pointY=:" + _pointY);

		// y--; // 设置Y轴坐标减1
		for (int i = 0; i < _numbers.size(); i++) {

			int number = _numbers.keyAt(i);
			Rect r = _numbers.valueAt(i);

			if (_pointX > r.left && _pointX < r.right && _pointY > r.top
					&& _pointY < r.bottom) {
				int clock = number;
				if (number == 0)
					clock = 12;

				if (_lastClock == clock) {
					break;
				}

				// DebugUtil.printDebugTag("judge",
				// "小时:" + number + "坐标:" + r.toString());

				checkValue(number);

				break;
			}

		}
	}

	private void checkValue(int number) {
		int clock = number;

		int checke = _checkList.get(clock);

		if (checke == 1) {
			// if (checkNext == 0 || allChecked() || allNOChecked()) {
			_checkList.put(number, 0);
			if (_valueListner != null)
				_valueListner.onChanged();
			// }
		} else {
			// if ( checkPrew== 1 || allChecked() || allNOChecked()) {
			_checkList.put(number, 1);
			if (_valueListner != null)
				_valueListner.onChanged();
			// }
		}
	}

	private boolean allChecked() {
		boolean allcheck = true;
		for (int i = 0; i < _checkList.size(); i++) {
			int check = _checkList.valueAt(i);
			if (check == 0) {
				allcheck = false;
				break;
			}
		}

		return allcheck;
	}

	private boolean allNOChecked() {
		boolean allNoCheck = true;
		for (int i = 0; i < _checkList.size(); i++) {
			int check = _checkList.valueAt(i);
			if (check == 1) {
				allNoCheck = false;
				break;
			}
		}

		return allNoCheck;
	}

	/**
	 * 当用户按键时调用
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) { // 当用户点击↑键时

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void run() {
		while (flag) {
			Draw(); // 调用自定义画画方法
			try {
				Thread.sleep(100); // 让线程休息50毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// 获取图片
	public Bitmap readBitmap(Resources r, int resId, int width) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		InputStream is = r.openRawResource(resId);

		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		Matrix matrix = new Matrix();

		int orginWidth = bitmap.getWidth();
		int orginHeith = bitmap.getHeight();

		float scale = (float) width / (float) orginWidth;

		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, orginWidth,
				orginHeith, matrix, true);
		return resizeBmp;
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}