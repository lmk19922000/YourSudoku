package com.yoursudoku;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.yoursudoku.command.Command;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class SudokuCanvasActivity extends Activity implements OnTouchListener, OnClickListener {
	SudokuBoard sudokuObject;
	
	Stack<Command> commandHistory;	// Support undo action
	
	Point size; // Size of the sudoku board
	
	SudokuCanvasView sudokuCanvas; // view to display sudoku
	int[][] sudokuInput; // Contain information about the sudoku
	List<Pair<Integer, Integer>> fixedNumbers;
	float fingerX, fingerY; // current position of user's finger
	
	Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9; // Button for input number

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sudokucanvas);
		
		initializeVariables();
		
		addCanvasView();
		
		initializeCompnentView();
	}

	private void initializeCompnentView() {
		btn1 = (Button)findViewById(R.id.button1);
		
		sudokuCanvas.setOnTouchListener(this);
		btn1.setOnClickListener(this);
	}

	private void addCanvasView() {
		sudokuCanvas = new SudokuCanvasView(this);

		// Set the position of the canvas relative to the activity view
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 50, 0, 0);
		// layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.textView1);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);

		layoutParams.height = size.x;
		layoutParams.width = size.x;

		// Add the canvas to the activity view
		ViewGroup v = (ViewGroup) getWindow().getDecorView().findViewById(
				R.id.RelativeLayout1);
		v.addView(sudokuCanvas, layoutParams);
	}

	@SuppressLint("NewApi")
	private void initializeVariables() {
		sudokuInput = new int[9][9];
				
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if ((i == 1 && j == 3) || (i == 1 && j == 6)){
					sudokuInput[i][j] = 0;
					continue;
				}
				sudokuInput[i][j] = j + 1;
			}
		}

		// Get the list of fixed input
		fixedNumbers = new Vector<Pair<Integer, Integer>>();
		fixedNumbers.add(new Pair<Integer, Integer>(2, 3));
		fixedNumbers.add(new Pair<Integer, Integer>(3, 7));
		fixedNumbers.add(new Pair<Integer, Integer>(5, 4));
		
		fingerX = fingerY = -1;
		
		commandHistory = new Stack<Command>();
		
		// Get size of the screen to decide size of the canvas
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
	}

	// View class to display sudoku
	public class SudokuCanvasView extends View {

		private Paint normalGridPaint, thickGridPaint, numberPaint,
				fixedNumberPaint, highlightPaint;

		public SudokuCanvasView(Context context) {
			super(context);
			normalGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			normalGridPaint.setStyle(Paint.Style.STROKE);
			normalGridPaint.setColor(Color.BLUE);
			normalGridPaint.setStrokeWidth(1);

			thickGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			thickGridPaint.setStyle(Paint.Style.STROKE);
			thickGridPaint.setColor(Color.BLUE);
			thickGridPaint.setStrokeWidth(6);

			numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			numberPaint.setStyle(Paint.Style.FILL);
			numberPaint.setColor(Color.RED);
			numberPaint.setTextSize(70);

			fixedNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			fixedNumberPaint.setStyle(Paint.Style.FILL);
			fixedNumberPaint.setColor(Color.GRAY);

			highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			highlightPaint.setStyle(Paint.Style.FILL);
			highlightPaint.setColor(Color.YELLOW);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			float X, Y;

			canvas.drawColor(Color.CYAN);

			// Draw squares containing fixed input
			for (int i = 0; i < fixedNumbers.size(); i++) {
				canvas.drawRect((float) canvas.getWidth() / 9 * fixedNumbers.get(i).getFirst(),
						(float) canvas.getHeight() / 9 * fixedNumbers.get(i).getSecond(),
						(float) canvas.getWidth() / 9 * (fixedNumbers.get(i).getFirst() + 1),
						(float) canvas.getHeight() / 9 * (fixedNumbers.get(i).getSecond() + 1),
						fixedNumberPaint);
			}

			// Highlight current square user touched
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (fingerX < (float) canvas.getWidth() / 9 * (i + 1)
							&& fingerX > (float) canvas.getWidth() / 9 * i
							&& fingerY > (float) canvas.getHeight() / 9 * j
							&& fingerY < (float) canvas.getHeight() / 9
									* (j + 1)) {
						canvas.drawRect((float) canvas.getWidth() / 9 * i,
								(float) canvas.getHeight() / 9 * j,
								(float) canvas.getWidth() / 9 * (i + 1),
								(float) canvas.getHeight() / 9 * (j + 1),
								highlightPaint);
					}
				}
			}

			// Draw vertical grids
			for (int i = 0; i < 10; i++) {
				if (i % 3 == 0) {
					canvas.drawLine(canvas.getWidth() / 9 * i, 0,
							canvas.getWidth() / 9 * i, canvas.getHeight(),
							thickGridPaint);
				} else {
					canvas.drawLine(canvas.getWidth() / 9 * i, 0,
							canvas.getWidth() / 9 * i, canvas.getHeight(),
							normalGridPaint);
				}
			}

			// Draw horizontal grids
			for (int i = 0; i < 10; i++) {
				if (i % 3 == 0) {
					canvas.drawLine(0, canvas.getHeight() / 9 * i,
							canvas.getWidth(), canvas.getHeight() / 9 * i,
							thickGridPaint);
				} else {
					canvas.drawLine(0, canvas.getHeight() / 9 * i,
							canvas.getWidth(), canvas.getHeight() / 9 * i,
							normalGridPaint);
				}
			}

			// Draw numbers to sudoku
			for (int i = 0; i < 9; i++) {
				X = ((float) canvas.getWidth() / 9 * i + (float) canvas
						.getWidth() / 9 * (i + 1)) / 2;
				for (int j = 0; j < 9; j++) {
					Y = ((float) canvas.getHeight() / 9 * j + (float) canvas
							.getHeight() / 9 * (j + 1)) / 2;
					// Need to reposition the coordinates to draw the number to
					// make it centralized
					canvas.drawText(String.valueOf(sudokuInput[i][j]), X - 18,
							Y + 25, numberPaint);
				}
			}

			invalidate();
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Get the current position of the finger
			fingerX = event.getX();
			fingerY = event.getY();
			break;
		case MotionEvent.ACTION_UP:

			break;
		case MotionEvent.ACTION_MOVE:

			break;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button1:
			boolean flag = false;
			if (fingerX!= -1 && fingerY != -1){
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						if (fingerX < (float) size.x / 9 * (i + 1)
								&& fingerX > (float) size.x / 9 * i
								&& fingerY > (float) size.x / 9 * j
								&& fingerY < (float) size.x / 9
										* (j + 1)) {
							sudokuInput[i][j] = 1;
							flag = true;
							break;
						}
					}
					if (flag){
						break;
					}
				}
			}
			break;
		}
	}

}
