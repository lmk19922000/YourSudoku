package com.yoursudoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class SudokuCanvasActivity extends Activity implements OnTouchListener {

	SudokuCanvasView sudokuCanvas; // view to display sudoku
	int[][] sudokuInput = new int[9][9]; // Contain information about the sudoku
	float fingerX, fingerY; // current position of user's finger

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sudokucanvas);

		sudokuCanvas = new SudokuCanvasView(this);

		// Set the position of the canvas relative to the activity view
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 50, 0, 0);
		// layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.textView1);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);

		// Get size of the screen to decide size of the canvas
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		layoutParams.height = size.x;
		layoutParams.width = size.x;

		// Add the canvas to the activity view
		ViewGroup v = (ViewGroup) getWindow().getDecorView().findViewById(
				R.id.RelativeLayout1);
		v.addView(sudokuCanvas, layoutParams);

		// Initialize the sudoku board
		initializeSudokuBoard();

		sudokuCanvas.setOnTouchListener(this);
	}

	// Get data from Duy to initialize the board
	private void initializeSudokuBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sudokuInput[i][j] = j + 1;
			}
		}
	}

	// View class to display sudoku
	public class SudokuCanvasView extends View {

		private Paint normalGridPaint, thickGridPaint, numberPaint,
				highlightPaint;

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
			numberPaint.setTextSize(50);
			numberPaint.setStrokeWidth(1);

			highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			highlightPaint.setStyle(Paint.Style.FILL);
			highlightPaint.setColor(Color.YELLOW);

			fingerX = fingerY = -1;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			float X, Y;

			canvas.drawColor(Color.CYAN);

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
					canvas.drawText(String.valueOf(sudokuInput[i][j]), X - 15,
							Y + 15, numberPaint);
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

}
