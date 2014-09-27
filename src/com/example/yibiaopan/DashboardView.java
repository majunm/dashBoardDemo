package com.example.yibiaopan;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * @author : 首席 author 胡帅兵
 * @author : 马俊
 * @date :2014年9月16日上午9:28:00
 * @version:v1.0+
 * @FileName:CustomView.java
 * @ProjectName:仪表盘demo
 * @PackageName:com.example.yibiaopan
 * @EnclosingType:
 * @Description:仪表盘demo
 */
public class DashboardView extends SurfaceView implements Callback, Runnable {
	/******************************* field *******************************/
	private SurfaceHolder holder; //
	private Paint paint; // 画笔
	private boolean flag = true;// 循环标记
	private Bitmap mDashBoardBitmap; // 仪表盘
	private Bitmap mLadderBitmap; // 阶梯
	private Bitmap mPointBitmap; // 仪表指针
	// private Bitmap mBackGroundBitmap;
	private int screenW; // 屏幕 宽度 right - left
	private int screenH; // 屏幕 宽度 right - left
	private Rect rect; // 矩形
	private Thread thread; // 线程
	private Canvas canvas; // 画板
	private int drawAngle = 0; // 画的角度,累计渐变
	private int rotateAngle = 0; // 旋转角度
	private int startDegree = 45;
	private boolean isMeasure = false;
	private int dashBoardInitialPointer; // 仪表盘初始化起点
	private int ladderInitialPointer; // 阶梯初始化起点
	private int pointerInitialPointer; // 指针初始化起点坐标
	private int centerPointer; // 中心点
	private List<BitmapDrawable> numberList;

	private int number;
	private int oneNumberWidth; // 一个数字的宽度
	private int oneNumberHeight; // 一个数字的宽度
	private Context context;

	private boolean beginPerformLogical = false;
	private int electricQuantityGradientVlaue; // 电量渐变值
	private int realValue;// 实际值
	private Paint ovalPaint;
	private int textSize = 20;
	private int screenType; // 屏幕类型

	public int[] result = new int[10];// 存放电量
	public int[] coordinate = new int[7];
	public int max;
	public int numberLength;
	private RectF oval; // 矩形

	/******************************* field *******************************/

	/**
	 * @Date:2014年9月16日上午9:28:15
	 * @param context
	 */
	public DashboardView(Context context) {
		this(context, null);
	}

	public DashboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			if (beginPerformLogical) {
				logicalDispose();
			}
			beginDraw();
			long end = System.currentTimeMillis();
			if (end - start < 40) {
				SystemClock.sleep(40 - (end - start));
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mDashBoardBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dash_board).copy(Bitmap.Config.ARGB_8888, true);
		mLadderBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ladder_log).copy(Bitmap.Config.ARGB_8888, true);
		mPointBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dash_board_point)
				.copy(Bitmap.Config.ARGB_8888, true);
		if (!isMeasure) {
			screenH = getHeight();
			screenW = getWidth();
			isMeasure = true;
			dashBoardInitialPointer = (screenW - mDashBoardBitmap.getWidth()) / 2;
			ladderInitialPointer = (screenW - mLadderBitmap.getWidth()) / 2;
			pointerInitialPointer = (screenW - mPointBitmap.getWidth()) / 2;
			centerPointer = screenW / 2;
			oval = new RectF(ladderInitialPointer, ladderInitialPointer,
					screenW - ladderInitialPointer, screenW
							- ladderInitialPointer);
			oneNumberWidth = numberList.get(0).getBitmap().getWidth();
			oneNumberHeight = numberList.get(0).getBitmap().getHeight();
			setFontSize(screenW, screenH);
		}

		// mBackGroundBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.signsec_dj_ll_blue).copy(Bitmap.Config.ARGB_8888,
		// true);
		rect = new Rect(0, 0, screenW, screenH);
		thread = new Thread(this);
		Log.e("majun95598", "screenW:" + screenW);
		Log.e("majun95598", "screenH:" + screenH);
		if (!thread.isAlive()) { // 判断是不是 在执行run方法
			thread.start(); // 开启线程绘制
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}

	/**
	 * 
	 * @data :2014年9月16日上午9:59:02
	 * @description :开始绘制
	 */
	public void beginDraw() {
		try {
			try {
				canvas = holder.lockCanvas(rect);
				canvas.drawColor(Color.WHITE);
				beginDrawDashBoard();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				holder.unlockCanvasAndPost(canvas); // 解锁画布 必须操作 否则 界面卡死
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @data :2014年9月16日上午9:38:18
	 * @description :逻辑处理 ,角度变化
	 */
	public void logicalDispose() {

		if (drawAngle >= rotateAngle) {
			drawAngle = rotateAngle;
			flag = false;
		} else {
			// 0 + 5
			drawAngle += numberLength == 0 ? 5 : numberLength;
		}
		if (realValue >= number) {
			realValue = number;
		} else {
			realValue += electricQuantityGradientVlaue;
		}
		Log.e("majun8", "drawAngle:----------->" + drawAngle);
		Log.e("majun8", "realValue:----------->" + realValue);
	}

	/**
	 * 
	 * @data :2014年9月16日上午10:01:26
	 * @description :绘制仪表盘
	 */
	public void beginDrawDashBoard() {
		drawBackgroundBitmap();
		drawElectricQuantityTitle(screenType);
		drawElectricQuantity(screenType);
		drawGradulation(screenType);
		rotateCoreCode();
	}

	/**
	 * @data :2014年9月25日上午11:35:16
	 * @description :刻画背景图片
	 */
	public void drawBackgroundBitmap() {
		drawArc();
		// drawLadderBitmap();
		canvas.drawBitmap(mDashBoardBitmap, dashBoardInitialPointer,
				dashBoardInitialPointer, paint);
	}

	/**
	 * @data :2014年9月25日下午4:43:04
	 * @description :画阶梯背景,取消弃用
	 */
	public void drawLadderBitmap() {
		canvas.drawBitmap(mLadderBitmap, ladderInitialPointer,
				ladderInitialPointer, paint);
	}

	/**
	 * @data :2014年9月25日上午9:19:49
	 * @description :指针旋转核心代码
	 */
	public void rotateCoreCode() {
		canvas.save();
		// 旋转
		canvas.rotate(startDegree + drawAngle, centerPointer, centerPointer);
		canvas.drawBitmap(mPointBitmap, pointerInitialPointer,
				pointerInitialPointer, paint);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measure(widthMeasureSpec, true),
				measure(heightMeasureSpec, false));
	}

	/**
	 * 
	 * @data :2014年9月25日上午11:36:47
	 * @param measureSpec
	 * @param isWidth
	 * @return
	 * @description :测量宽高
	 */
	public int measure(int measureSpec, boolean isWidth) {
		int result;
		int size = MeasureSpec.getSize(measureSpec);
		int mode = MeasureSpec.getMode(measureSpec);
		int padding = isWidth ? getPaddingLeft() + getPaddingRight()
				: getPaddingBottom() + getPaddingTop();
		if (mode == MeasureSpec.EXACTLY) { // 精确模式
			result = size;
		} else {
			result = isWidth ? getSuggestedMinimumWidth()
					: getSuggestedMinimumHeight();
			result += padding;
			if (mode == MeasureSpec.AT_MOST) { // 最大模式
				if (isWidth) {
					result = Math.max(result, size);
				} else {
					result = Math.min(result, size);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @data :2014年9月25日下午1:02:09
	 * @param number
	 * @description :设置累计电量啦
	 */
	private void setNumber(int number) {
		this.number = Math.abs(number);
		numberLength = String.valueOf(this.number).length();
		Log.e("majun8", "numberLength---->" + numberLength);
	}

	/**
	 * @data :2014年9月25日下午1:19:36
	 * @description :计算电量渐变值
	 */
	private void calculateGradientValue() {
		if (rotateAngle != 0) {
			electricQuantityGradientVlaue = (int) (((float) number)
					/ ((float) rotateAngle) * numberLength);
		}
		Log.e("majun8", "electricQuantityGradientVlaue:--->"
				+ electricQuantityGradientVlaue);
	}

	/**
	 * 
	 * @data :2014年9月25日下午1:02:31
	 * @param progress
	 * @description :设置进度,占仪表盘进度的多少
	 */
	private void setProgress(int progress) {
		calculateDegree(Math.abs(progress));
		calculateGradientValue();
		if (!isBeginPerformLogical()) {
			setBeginPerformLogical(true);
		}
	}

	/**
	 * 
	 * @data :2014年9月25日下午12:14:25
	 * @param coordinate
	 *            长度必须为7 不要捣乱
	 * @description :设置刻度值
	 */
	private void setCoordinate(int[] coordinate) {
		if (coordinate.length < 7) {
			Toast.makeText(context, "只接受长度为7的数组!", Toast.LENGTH_SHORT).show();
			return;
		}
		for (int i = 0; i < coordinate.length; i++) {
			coordinate[i] = Math.abs(coordinate[i]); // 赋值绝对值,防止有负号
		}
		this.coordinate = coordinate;
		max = coordinate[6];
	}

	/**
	 * 
	 * @data :2014年9月25日上午11:12:27
	 * @param progress
	 * @description :计算旋转角度
	 */
	public void calculateDegree(int progress) {
		if (progress >= max) {
			rotateAngle = 270;
		} else {
			if (progress <= coordinate[2]) {
				rotateAngle = (int) (90 * ((float) progress / (float) coordinate[2]));
			} else if (progress > coordinate[2] && progress <= coordinate[4]) {
				rotateAngle = 90 + (int) (90 * (((float) progress - (float) coordinate[2]) / ((float) coordinate[4] - (float) coordinate[2])));
			} else {
				rotateAngle = 180 + (int) (90 * (((float) progress - (float) coordinate[4]) / ((float) coordinate[6] - (float) coordinate[4])));
			}
		}
		Log.e("majun8", "rotateAngle------------:--->" + rotateAngle);
	}

	/**
	 * 
	 * @data :2014年9月25日下午1:35:09
	 * @description :画用电量
	 */
	public void drawElectricQuantity(int screenType) {
		for (int i = 0; i < numberLength; i++) {
			// 5550
			result[i] = Integer.parseInt(String.valueOf(number).charAt(i) + ""); // 数据矫正
		}
		int temporary = realValue;// 临时变量
		for (int i = numberLength; i > 0; i--) {
			Log.e("majun8", "--------------------------------->" + temporary);
			result[i - 1] = temporary % 10; // 1000 = 4 3 2 1
			temporary = temporary / 10;
		}
		switch (screenType) {
		case 1:
			for (int i = 0; i < numberLength; i++) {
				canvas.drawBitmap(numberList.get(result[i]).getBitmap(),
						(screenW - oneNumberWidth * numberLength) / 2
								+ oneNumberWidth * i + i * 2,
						mDashBoardBitmap.getHeight() - oneNumberWidth * 4
								+ oneNumberHeight, paint);
			}
			break;
		case 2:
			for (int i = 0; i < numberLength; i++) {
				canvas.drawBitmap(numberList.get(result[i]).getBitmap(),
						(screenW - oneNumberWidth * numberLength) / 2
								+ oneNumberWidth * i + i * 2,
						mDashBoardBitmap.getHeight() - oneNumberWidth * 5
								+ oneNumberHeight * 0, paint);
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * @data :2014年9月25日上午11:16:43
	 * @description :画累计用电量 (标示作用)
	 */
	public void drawElectricQuantityTitle(int screenType) {
		String text = "累计用电量";
		float length = paint.measureText(text, 0, text.length());
		paint.setColor(Color.BLACK);
		paint.setTextSize(textSize);
		switch (screenType) {
		case 1:
			canvas.drawText(text, (screenW - length) / 2,
					mDashBoardBitmap.getHeight() - oneNumberWidth * 4
							+ oneNumberHeight * 2.5f + 2f, paint);
			break;
		case 2:
			canvas.drawText(text, (screenW - length) / 2,
					mDashBoardBitmap.getHeight() - oneNumberWidth * 5
							+ oneNumberHeight * 1.5f + 2f, paint);
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * @data :2014年9月25日上午11:17:38
	 * @param drawAngle
	 * @description :设置角度
	 */
	public void setAngel(int drawAngle) {
		this.drawAngle = drawAngle;
	}

	/**
	 * 
	 * @data :2014年9月25日上午11:27:35
	 * @description :画表盘刻度值
	 */
	public void drawGradulation(int screenType) {
		float length = paint.measureText((coordinate[3] + ""), 0,
				(coordinate[3] + "").length());
		switch (screenType) {
		case 1:
			canvas.drawText(coordinate[0] + "", oneNumberHeight * 4
					+ oneNumberWidth + 5, mDashBoardBitmap.getWidth(), paint);
			canvas.drawText(coordinate[6] + "", oneNumberHeight * 5
					+ mLadderBitmap.getWidth() / 2,
					mDashBoardBitmap.getWidth(), paint);

			canvas.drawText(coordinate[1] + "", oneNumberHeight * 3 + 5,
					mDashBoardBitmap.getWidth() / 2 + oneNumberHeight * 2.5f,
					paint);
			canvas.drawText(coordinate[5] + "", oneNumberHeight * 6
					+ mLadderBitmap.getWidth() / 2 + oneNumberWidth,
					mDashBoardBitmap.getWidth() / 2 + oneNumberHeight * 2.5f,
					paint);

			canvas.drawText(coordinate[2] + "", oneNumberHeight * 4
					+ oneNumberWidth + 5, mDashBoardBitmap.getWidth() / 2
					- oneNumberHeight - 5, paint);
			canvas.drawText(coordinate[4] + "", oneNumberHeight * 5
					+ mLadderBitmap.getWidth() / 2, mDashBoardBitmap.getWidth()
					/ 2 - oneNumberHeight - 5, paint);

			canvas.drawText(coordinate[3] + "", (screenW - length) / 2,
					dashBoardInitialPointer + oneNumberHeight + oneNumberWidth,
					paint);
			break;
		case 2:
			canvas.drawText(coordinate[0] + "", oneNumberHeight * 3 + 10,
					mDashBoardBitmap.getWidth() - oneNumberHeight
							- oneNumberWidth + 10, paint);
			canvas.drawText(coordinate[6] + "", oneNumberHeight * 4
					+ mLadderBitmap.getWidth() / 2 - 1,
					mDashBoardBitmap.getWidth() - oneNumberHeight
							- oneNumberWidth + 10, paint);

			canvas.drawText(coordinate[1] + "", oneNumberHeight * 2 + 5
					- oneNumberWidth + 10, mDashBoardBitmap.getWidth() / 2
					+ oneNumberHeight * 1.5f - oneNumberWidth, paint);
			canvas.drawText(coordinate[5] + "", oneNumberHeight * 5
					+ mLadderBitmap.getWidth() / 2 + oneNumberWidth,
					mDashBoardBitmap.getWidth() / 2 + oneNumberHeight * 1.5f
							- oneNumberWidth, paint);

			canvas.drawText(coordinate[2] + "", oneNumberHeight * 3
					+ oneNumberWidth * 0 + 10, mDashBoardBitmap.getWidth() / 2
					- oneNumberHeight * 3 + 5, paint);
			canvas.drawText(coordinate[4] + "", oneNumberHeight * 4
					+ mLadderBitmap.getWidth() / 2, mDashBoardBitmap.getWidth()
					/ 2 - oneNumberHeight * 3 + 5, paint);

			canvas.drawText(coordinate[3] + "", (screenW - length) / 2,
					dashBoardInitialPointer + oneNumberHeight + oneNumberWidth,
					paint);
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * @data :2014年9月25日下午12:23:21
	 * @param coordinate
	 *            刻度
	 * @param number
	 *            当前电量
	 * @param progress
	 *            进度
	 * @description :按顺序执行
	 */
	public void executeTask(int[] coordinate, int number, int progress) {
		setCoordinate(coordinate);
		setNumber(number);
		setProgress(progress);
	}

	/**
	 * 
	 * @data :2014年9月25日下午12:46:29
	 * @return
	 * @description :获取执行业务逻辑状态
	 */
	public boolean isBeginPerformLogical() {
		return beginPerformLogical;
	}

	/**
	 * 
	 * @data :2014年9月25日下午1:05:23
	 * @param beginPerformLogical
	 *            ture?开始执行逻辑:不执行逻辑
	 * @description :设置逻辑处理开关
	 */
	public void setBeginPerformLogical(boolean beginPerformLogical) {
		this.beginPerformLogical = beginPerformLogical;
	}

	/**
	 * 
	 * @data :2014年9月25日下午4:44:05
	 * @description :绘制圆弧
	 */
	public void drawArc() {
		if (drawAngle <= 90) {
			ovalPaint.setColor(Color.GREEN);
			canvas.drawArc(oval, startDegree + 90, drawAngle, false, ovalPaint);
		} else if (drawAngle > 90 && drawAngle <= 180) {
			ovalPaint.setColor(Color.GREEN);
			canvas.drawArc(oval, startDegree + 90, 90, false, ovalPaint);
			ovalPaint.setColor(Color.parseColor("#ffbd00"));
			canvas.drawArc(oval, startDegree + 180, drawAngle - 90, false,
					ovalPaint);
		} else {
			if (drawAngle > 270) {
				drawAngle = 270;
			}
			ovalPaint.setColor(Color.GREEN);
			canvas.drawArc(oval, startDegree + 90, 90, false, ovalPaint);
			ovalPaint.setColor(Color.parseColor("#ffbd00"));
			canvas.drawArc(oval, startDegree + 180, drawAngle - 90, false,
					ovalPaint);
			ovalPaint.setColor(Color.RED);
			canvas.drawArc(oval, startDegree + 270, drawAngle - 180, false,
					ovalPaint);
		}
	}

	/**
	 * @data :2014年9月25日下午4:47:58
	 * @param context
	 * @description :初始化工作
	 */
	public void init(Context context) {
		this.context = context;
		holder = getHolder();
		holder.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.argb(255, 207, 60, 11));
		paint.setTextSize(22);
		ovalPaint = new Paint();
		ovalPaint.setAntiAlias(true);
		ovalPaint.setStyle(Paint.Style.STROKE); // 空心
		ovalPaint.setStrokeWidth(7);
		numberList = new ArrayList<BitmapDrawable>();
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.zero));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.one));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.two));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.three));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.four));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.five));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.six));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.sevent));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.eight));
		numberList.add((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.nine));
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	/**
	 * @data :2014年9月26日上午8:42:14
	 * @description :设置字体大小
	 */
	private void setFontSize(int width, int height) {
		switch (width) {
		case 1080:
			textSize = 40;
			screenType = 1;
			break;
		case 540:
			textSize = 20;
			screenType = 2;
			break;
		default:
			textSize = 22;
			break;
		}
	}

}
