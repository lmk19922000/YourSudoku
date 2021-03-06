package com.yoursudoku;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.yoursudoku.database.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SudokuCanvasActivity extends Activity implements OnTouchListener,
		OnClickListener {
	SudokuGame sudokuGameObject;

	Stack<SudokuGame> commandStack; 			// Support undo action

	Point size; 								// Size of the sudoku board (in terms of phone screen)

	SudokuCanvasView sudokuCanvas; 				// view to display sudoku
	Integer[][] sudokuInput; 					// Contain information about the sudoku
	List<Pair<Integer, Integer>> fixedCells;	// Contain fixed cells at the beginning 
	Pair<Integer, Integer> violatedCell;		
	float fingerX, fingerY; 					// current position of user's finger

	Button btn1, btn2, btn3, btn4,
	btn5, btn6, btn7, btn8, btn9; // Button for input number
	
	Button btnUndo, btnClear;
	ToggleButton btnDraft;
	
	boolean inDraftMode, sudokuSolved;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sudokucanvas);

		initializeVariables();

		addCanvasView();

		initializeCompnentView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sudoku_action, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_hint:
	            showHint();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showHint() {
		SudokuBoard solution = sudokuGameObject.getSudokuBoard().getSolvedSudokuBoard();
		
		boolean flag = false;
		if (fingerX != -1 && fingerY != -1) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (fingerX < (float) size.x / 9 * (i + 1)
							&& fingerX > (float) size.x / 9 * i
							&& fingerY > (float) size.x / 9 * j
							&& fingerY < (float) size.x / 9 * (j + 1)) {
						// Disable set Cell Value for fixed cells
						boolean innerFlag = false;
						for (int k = 0; k <fixedCells.size(); k++){
							if (fixedCells.get(k).getFirst() == j && fixedCells.get(k).getSecond() == i){
								innerFlag = true;
								break;
							}
						}
						
						if (innerFlag){
							flag = true;
							break;
						}
						
						SudokuBoard oldSudokuBoard = new SudokuBoard(sudokuGameObject.getSudokuBoard());
						Vector<Vector<Vector<Integer>>> oldDraftList = sudokuGameObject.copyDraftBoard(sudokuGameObject.draftBoard);
						
						Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> result = sudokuGameObject.setCellValue(j, i, solution.getCellValue(j, i));
						Log.i("cell value", String.valueOf(sudokuGameObject.getCellValue(j, i)));
						Log.i("size of draft list", String.valueOf(sudokuGameObject.getCellDraftList(j, i).size()));
						if(result.getFirst() == SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS){
							violatedCell.setFirst(-1);
							violatedCell.setSecond(-1);
							commandStack.push(new SudokuGame(oldSudokuBoard, oldDraftList));
							sudokuSolved = true;
							for (int k = 0; k <9; k++){
								for (int l = 0; l <9; l++){
									if (sudokuGameObject.getCellValue(k, l) == 0){
										sudokuSolved = false;
										break;
									}
								}
							}
							if (sudokuSolved){
								Log.i("SOLUTION STATUS", "SOLVED");
								showFinishDialog();
							}
						} else {
							violatedCell.setFirst(result.getSecond().getFirst());
							violatedCell.setSecond(result.getSecond().getSecond());
						}
						
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		}
		
	}

	private void showFinishDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.finish_dialog);

		Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, go to main menu the custom dialog
		dialogButtonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SudokuCanvasActivity.this, HomeActivity.class);
				startActivity(myIntent);
			}
		});

		Button dialogButtonShare = (Button) dialog.findViewById(R.id.dialogButtonShare);
		// if button is clicked, go to main menu the custom dialog
		dialogButtonShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "I am just solved a puzzle on Your Sudoku!");
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Sudoku Score");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share with your friends"));
			}
		});
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		dialog.show();

		dialog.getWindow().setAttributes(lp);
		
	}

	private void initializeCompnentView() {
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		btn5 = (Button) findViewById(R.id.button5);
		btn6 = (Button) findViewById(R.id.button6);
		btn7 = (Button) findViewById(R.id.button7);
		btn8 = (Button) findViewById(R.id.button8);
		btn9 = (Button) findViewById(R.id.button9);
		btnUndo = (Button) findViewById(R.id.buttonUndo);
		btnClear = (Button) findViewById(R.id.buttonClear);
		btnDraft = (ToggleButton) findViewById(R.id.buttonDraft);
		
		btnDraft.setChecked(false);
		
		sudokuCanvas.setOnTouchListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btnUndo.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnDraft.setOnClickListener(this);

		/*
		 * TextView tv = (TextView)findViewById(R.id.textView1); Database db =
		 * Database.getDatabase(); tv.setText(db.execute(this));
		 */
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
		sudokuInput = new Integer[][] { { 5, 7, 9, 0, 2, 4, 0, 0, 1 },
				{ 3, 0, 2, 0, 1, 0, 9, 7, 0 }, { 0, 0, 0, 7, 6, 0, 0, 0, 2 },
				{ 7, 3, 0, 2, 0, 1, 0, 6, 9 }, { 8, 0, 4, 0, 9, 0, 3, 0, 5 },
				{ 9, 0, 0, 0, 0, 3, 0, 1, 0 }, { 0, 9, 0, 0, 7, 5, 0, 4, 0 },
				{ 0, 0, 8, 0, 3, 0, 2, 0, 7 }, { 6, 0, 0, 1, 0, 2, 8, 9, 0 } };

		sudokuGameObject = new SudokuGame(sudokuInput);
		//sudokuBoardObject = sudokuGameObject.getSudokuBoard();
		
		// Get the list of fixed input
		fixedCells = new Vector<Pair<Integer, Integer>>();
		
		for (int i = 0; i <9; i++){
			for (int j = 0; j <9; j++){
				if (sudokuGameObject.getCellValue(i, j) != 0){
					fixedCells.add(new Pair<Integer, Integer>(i,j));
				}
			}
		}
		
		violatedCell = new Pair<Integer, Integer>(-1, -1);
		
		fingerX = fingerY = -1;

		commandStack = new Stack<SudokuGame>();

		// Get size of the screen to decide size of the canvas
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		
		inDraftMode = false;
		sudokuSolved = false;
	}

	// View class to display sudoku
	public class SudokuCanvasView extends View {

		private Paint normalGridPaint, thickGridPaint, numberPaint,
				fixedCellPaint, highlightPaint, violatedPaint, smallNumberPaint;

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
			numberPaint.setColor(Color.GREEN);
			numberPaint.setTextSize(70);

			fixedCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			fixedCellPaint.setStyle(Paint.Style.FILL);
			fixedCellPaint.setColor(Color.GRAY);

			highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			highlightPaint.setStyle(Paint.Style.FILL);
			highlightPaint.setColor(Color.YELLOW);
			
			violatedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			violatedPaint.setStyle(Paint.Style.FILL);
			violatedPaint.setColor(Color.RED);
			
			smallNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			smallNumberPaint.setStyle(Paint.Style.FILL);
			smallNumberPaint.setColor(Color.GREEN);
			smallNumberPaint.setTextSize(20);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			float X, Y, x, y;

			canvas.drawColor(Color.WHITE);

			// Draw squares containing fixed input
			for (int i = 0; i < fixedCells.size(); i++) {
				canvas.drawRect((float) canvas.getWidth() / 9
						* fixedCells.get(i).getSecond(),
						(float) canvas.getHeight() / 9
								* fixedCells.get(i).getFirst(),
						(float) canvas.getWidth() / 9
								* (fixedCells.get(i).getSecond() + 1),
						(float) canvas.getHeight() / 9
								* (fixedCells.get(i).getFirst() + 1),
						fixedCellPaint);
			}
			
			// Draw violated cell (if have)
			if (violatedCell.getFirst() != -1 && violatedCell.getSecond() != -1){
				canvas.drawRect((float) canvas.getWidth() / 9
						* violatedCell.getSecond(),
						(float) canvas.getHeight() / 9
								* violatedCell.getFirst(),
						(float) canvas.getWidth() / 9
								* (violatedCell.getSecond() + 1),
						(float) canvas.getHeight() / 9
								* (violatedCell.getFirst() + 1),
						violatedPaint);
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
				Y = ((float) canvas.getHeight() / 9 * i + (float) canvas
						.getHeight() / 9 * (i + 1)) / 2;
				for (int j = 0; j < 9; j++) {
					X = ((float) canvas.getWidth() / 9 * j + (float) canvas
							.getWidth() / 9 * (j + 1)) / 2;
					// Need to reposition the coordinates to draw the number to
					// make it centralized
					if (sudokuGameObject.getCellValue(i, j) != 0){
						canvas.drawText(String.valueOf(sudokuGameObject.getCellValue(i, j)), X - 18,
								Y + 25, numberPaint);
					} else if (!sudokuGameObject.getCellDraftList(i, j).isEmpty()){
						for (int k = 0; k <=sudokuGameObject.getCellDraftList(i, j).size()/3; k++){
							y = ((float) canvas.getHeight() / 9 * (i+1) - (float) canvas
									.getHeight() / 9 * i) / 3 * (k+1) + (float) canvas.getHeight() / 9 * i;
							for (int l = 0; l <sudokuGameObject.getCellDraftList(i, j).size()-k*3 && l < 3; l++){
								x = ((float) canvas.getWidth() / 9 * (j+1) - (float) canvas
										.getWidth() / 9 * j) / 3 * (l+1) + (float) canvas.getWidth() / 9 * j;
								canvas.drawText(String.valueOf(sudokuGameObject.getCellDraftList(i, j).get(k*3+l)), x - 18,
										y, smallNumberPaint);
							}
						}
					}
				}
			}

			invalidate();
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
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
		switch (v.getId()) {
		case R.id.button1:
			placeNumber(1);
			break;
		case R.id.button2:
			placeNumber(2);
			break;
		case R.id.button3:
			placeNumber(3);
			break;
		case R.id.button4:
			placeNumber(4);
			break;
		case R.id.button5:
			placeNumber(5);
			break;
		case R.id.button6:
			placeNumber(6);
			break;
		case R.id.button7:
			placeNumber(7);
			break;
		case R.id.button8:
			placeNumber(8);
			break;
		case R.id.button9:
			placeNumber(9);
			break;
		case R.id.buttonDraft:
			if (btnDraft.isChecked()){
				inDraftMode = true;
				Log.i("draft mode", "on");
			} else{
				inDraftMode = false;
				Log.i("draft mode", "off");
			}
			break;
		case R.id.buttonUndo:
			if (!commandStack.isEmpty()){
				SudokuGame prev = commandStack.pop();
				sudokuGameObject = prev;
			}
			break;
		case R.id.buttonClear:
			placeNumber(0);
			break;
		}
	}

	private void placeNumber(int num) {
		boolean flag = false;
		if (fingerX != -1 && fingerY != -1) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (fingerX < (float) size.x / 9 * (i + 1)
							&& fingerX > (float) size.x / 9 * i
							&& fingerY > (float) size.x / 9 * j
							&& fingerY < (float) size.x / 9 * (j + 1)) {
						// Disable set Cell Value for fixed cells
						boolean innerFlag = false;
						for (int k = 0; k <fixedCells.size(); k++){
							if (fixedCells.get(k).getFirst() == j && fixedCells.get(k).getSecond() == i){
								innerFlag = true;
								break;
							}
						}
						
						if (innerFlag){
							flag = true;
							break;
						}
						
						SudokuBoard oldSudokuBoard = new SudokuBoard(sudokuGameObject.getSudokuBoard());
						Vector<Vector<Vector<Integer>>> oldDraftList = sudokuGameObject.copyDraftBoard(sudokuGameObject.draftBoard);
						
						if (num == 0){
							sudokuGameObject.clearCell(j, i);
							commandStack.push(new SudokuGame(oldSudokuBoard, oldDraftList));
							flag = true;
							break;
						}
						
						
						if (!inDraftMode){
							Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> result = sudokuGameObject.setCellValue(j, i, num);
							Log.i("cell value", String.valueOf(sudokuGameObject.getCellValue(j, i)));
							Log.i("size of draft list", String.valueOf(sudokuGameObject.getCellDraftList(j, i).size()));
							if(result.getFirst() == SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS){
								violatedCell.setFirst(-1);
								violatedCell.setSecond(-1);
								commandStack.push(new SudokuGame(oldSudokuBoard, oldDraftList));
								if (sudokuGameObject.isSolved()){
									showFinishDialog();
								}
							} else {
								violatedCell.setFirst(result.getSecond().getFirst());
								violatedCell.setSecond(result.getSecond().getSecond());
							}
						} else{
							Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> result = sudokuGameObject.addDraftNumber(j, i, num);
							Log.i("cell value", String.valueOf(sudokuGameObject.getCellValue(j, i)));
							Log.i("size of draft list", String.valueOf(sudokuGameObject.getCellDraftList(j, i).size()));
							if(result.getFirst() == SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS){
								violatedCell.setFirst(-1);
								violatedCell.setSecond(-1);
								commandStack.push(new SudokuGame(oldSudokuBoard, oldDraftList));
								if (sudokuGameObject.isSolved()){
									showFinishDialog();
								}
							} else {
								violatedCell.setFirst(result.getSecond().getFirst());
								violatedCell.setSecond(result.getSecond().getSecond());
							}
						}
						
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		}
	}

}
