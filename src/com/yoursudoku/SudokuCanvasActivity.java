package com.yoursudoku;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class SudokuCanvasActivity extends Activity implements OnTouchListener{
	
	SudokuCanvasView sudokuCanvas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sudokucanvas);
		
		sudokuCanvas = new SudokuCanvasView(this);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// layoutParams.setMargins(10,10,10,10);
		//layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.textView1);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		
		layoutParams.height= 300;
		layoutParams.width = 300;
		
		//ViewGroup v = (ViewGroup) getWindow().getDecorView().getRootView();
		ViewGroup v = (ViewGroup) getWindow().getDecorView().findViewById(R.id.RelativeLayout1);
		v.addView(sudokuCanvas, layoutParams);
	}
	
	public class SudokuCanvasView extends View{
		
		private Paint myPaint;
		
		public SudokuCanvasView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setColor(Color.BLUE);
			myPaint.setStrokeWidth(3);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			
			canvas.drawColor(Color.GREEN);
			float initialX = canvas.getWidth() / 2;
			float initialY = canvas.getHeight() / 2;
						
				canvas.drawCircle(initialX, initialY, canvas.getWidth() / 10,
						myPaint);
			
			invalidate();
		}
		
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
