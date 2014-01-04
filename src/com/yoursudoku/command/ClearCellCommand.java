package com.yoursudoku.command;

import com.yoursudoku.SudokuBoard;

public class ClearCellCommand implements Command{
	SudokuBoard oldSudokuObj;	// Snapshot of the old board to facilitate undo
	SudokuBoard currentSudokuObj;
	
	public ClearCellCommand(SudokuBoard oldSudokuObj) {
		super();
		this.oldSudokuObj = new SudokuBoard(oldSudokuObj);
		this.currentSudokuObj = oldSudokuObj;
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
