package com.yoursudoku.command;

import com.yoursudoku.SudokuBoard;

public class SetCellCommand implements Command{
	SudokuBoard oldSudokuObj;	// Snapshot of the old board to facilitate undo
	SudokuBoard currentSudokuObj;
	int x, y, value;
	
	public SetCellCommand(SudokuBoard oldSudokuObj, int x, int y, int value) {
		super();
		this.oldSudokuObj = new SudokuBoard(oldSudokuObj);
		currentSudokuObj = oldSudokuObj;
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@Override
	public void execute() {
		currentSudokuObj.setCellValue(x, y, value);
	}

	@Override
	public void undo() {
		//TODO: Use new API to undo
	}

}
