package com.example.yibiaopan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class ActivityTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(new SampleView(this));
		setContentView(new CustomView(this));
	}

	static class CustomView extends View {

		private Paint paint;

		public CustomView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.GREEN);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawColor(Color.WHITE);
			canvas.drawCircle(200, 200, 200, paint);
			paint.setColor(Color.YELLOW);
			canvas.drawCircle(200, 200, 100, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(0, 490, getWidth(), 500, paint);
			paint.setColor(Color.GREEN);
			canvas.drawLine(0, 500, 200, 500, paint);
			canvas.drawCircle(200, 600, 100, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(200, 0, 200, 600, paint);
			canvas.translate(0, 500);
			canvas.drawText("hello", 0, 0, paint);

		}

		/**
		 * @Date:2014年9月26日下午11:25:57
		 * @param context
		 */
		public CustomView(Context context) {
			this(context, null, 0);
		}

		public CustomView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

	}

	static class SampleView extends View {
		private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.CLIP_TO_LAYER_SAVE_FLAG;

		private Paint mPaint;
		private Path mPath;

		public SampleView(Context context) {
			super(context);
			setFocusable(true);

			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(6);
			mPaint.setTextSize(16);
			mPaint.setTextAlign(Paint.Align.RIGHT);
			mPath = new Path();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.save();
			canvas.drawColor(Color.GRAY);
			mPaint.setColor(Color.RED);
			canvas.drawLine(10, 0, 10, 10, mPaint);
			canvas.drawLine(0, 10, 10, 10, mPaint);
			canvas.restore();

			canvas.save();
			canvas.translate(11, 11); // 移动画布 10.10 起始点
			drawScene(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate(160, 11); // 移动画布 10.10 起始点
			canvas.clipRect(10, 10, 90, 90);
			canvas.clipRect(30, 30, 70, 70, Region.Op.DIFFERENCE);
			drawScene(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate(10, 160);
			mPath.reset();
			canvas.clipPath(mPath); // makes the clip empty
			mPath.addCircle(50, 50, 50, Path.Direction.CCW);
			canvas.clipPath(mPath, Region.Op.REPLACE);
			drawScene(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate(160, 160);
			canvas.clipRect(0, 0, 60, 60);
			canvas.clipRect(40, 40, 100, 100, Region.Op.UNION);
			drawScene(canvas);
			canvas.restore();
			
			canvas.save();  
            canvas.translate(10, 310);  
            canvas.clipRect(0, 0, 60, 60);  
            canvas.clipRect(40, 40, 100, 100, Region.Op.XOR);  
            drawScene(canvas);  
            canvas.restore(); 
            
            canvas.save();  
            canvas.translate(160, 310);  
            canvas.clipRect(0, 0, 60, 60);  
            canvas.clipRect(40, 40, 100, 100, Region.Op.REVERSE_DIFFERENCE);  
            drawScene(canvas);  
            canvas.restore(); 

		}

		protected void onDraw2(Canvas canvas) {
			mPaint.setColor(Color.BLUE);
			canvas.save();
			canvas.drawColor(Color.WHITE);
			canvas.drawLine(100, 0, 100, 450, mPaint);
			canvas.drawCircle(150, 50, 50, mPaint);// radius + translateX = x
													// radius + translateY = y
			// canvas.translate(0, 150);
			mPaint.setColor(Color.RED);
			canvas.drawLine(95, 95, 295, 95, mPaint);
			canvas.clipRect(new Rect(100, 100, 300, 300));
			canvas.drawColor(Color.BLUE);// 裁剪区域的rect变为蓝色
			canvas.drawRect(new Rect(0, 0, 100, 100), mPaint);// 在裁剪的区域之外，不能显示
			// canvas.drawCircle(150, 150, 150, mPaint);// 在裁剪区域之内，能显示
			canvas.drawCircle(150, 150, 50, mPaint);// 在裁剪区域之内，能显示
			canvas.restore();
		}

		private void drawScene(Canvas canvas) {
			canvas.clipRect(0, 0, 100, 100);

			canvas.drawColor(Color.WHITE);

			mPaint.setColor(Color.RED);
			canvas.drawLine(0, 0, 100, 100, mPaint);

			mPaint.setColor(Color.GREEN);
			canvas.drawCircle(30, 70, 30, mPaint);

			mPaint.setColor(Color.BLUE);
			canvas.drawText("Clipping", 100, 30, mPaint);
		}

		protected void onDraw1(Canvas canvas) {
			canvas.drawColor(Color.WHITE);

			canvas.translate(500, 200);

			canvas.saveLayerAlpha(0, 0, 200, 200, 0x88, LAYER_FLAGS);

			mPaint.setColor(Color.RED);
			canvas.drawCircle(100, 100, 100, mPaint);
			mPaint.setColor(Color.BLUE);
			canvas.drawCircle(125, 125, 75, mPaint);

			canvas.restore();

		}
	}
}