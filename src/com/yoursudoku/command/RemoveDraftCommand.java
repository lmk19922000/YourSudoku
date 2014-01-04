package com.yoursudoku.command;

import com.yoursudoku.SudokuBoard;

public class RemoveDraftCommand implements Command{
	SudokuBoard oldSudokuObj;	// Snapshot of the old board to facilitate undo
	SudokuBoard currentSudokuObj;
	int x, y, value;
	
	public RemoveDraftCommand(SudokuBoard oldSudokuObj, int x, int y, int value) {
		super();
		this.oldSudokuObj = new SudokuBoard(oldSudokuObj);
		this.currentSudokuObj = oldSudokuObj;
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

}
