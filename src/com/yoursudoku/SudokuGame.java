package com.yoursudoku;

import java.util.List;
import java.util.Vector;

import android.util.Log;

/**
 * Implements a class representing a Sudoku Game
 * 
 * @author truongduy134@gmail.com (Duy Nguyen Truong)
 *
 */
public class SudokuGame {
	// Class attributes
	SudokuBoard gameBoard;
	Vector<Vector<Vector<Integer>>> draftBoard;
	
	/**
	 * Default constructor: Creates an empty game board and draft board
	 */
	public SudokuGame() {
		this(new SudokuBoard());
	}
	
	/**
	 * 
	 * @param gameBoard
	 */
	public SudokuGame(SudokuBoard sudokuBoard) {
		gameBoard = new SudokuBoard(sudokuBoard);
		createEmptyDraftBoard();
	}
	
	/**
	 * 
	 * @param boardData
	 * @param difficultyLevel
	 * @param maxPointEarned
	 */
	public SudokuGame(List<List<Integer>> boardData, int difficultyLevel, int maxPointEarned) {
		gameBoard = new SudokuBoard(boardData, difficultyLevel, maxPointEarned);
		createEmptyDraftBoard();
	}
	
	/**
	 * 
	 * @param boardData
	 * @param difficultyLevel
	 * @param maxPointEarned
	 */
	public SudokuGame(Integer[][] boardData, int difficultyLevel, int maxPointEarned) {
		gameBoard = new SudokuBoard(boardData, difficultyLevel, maxPointEarned);
		createEmptyDraftBoard();
	}
	
	/**
	 * 
	 * @param boardData
	 */
	public SudokuGame(Integer[][] boardData) {
		gameBoard = new SudokuBoard(boardData);
		createEmptyDraftBoard();
	}
	
	/**
	 * 
	 * @param boardData
	 */
	public SudokuGame(List<List<Integer>> boardData) {
		gameBoard = new SudokuBoard(boardData);
		createEmptyDraftBoard();
	}
	
	/**
	 *
	 */
	private void createEmptyDraftBoard() {
		int boardSize = gameBoard.getBoardSize();
		draftBoard = new Vector<Vector<Vector<Integer>>>();
		for (int r = 0; r < boardSize; r++) {
			draftBoard.add(new Vector<Vector<Integer>>());
			for (int c = 0; c < boardSize; c++)
				draftBoard.get(r).add(new Vector<Integer>());
		}
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Integer getCellValue(int row, int col) {
		return this.gameBoard.getCellValue(row, col);
	}
	
	/** 
	 * 
	 * @param row
	 * @param col
	 * @param value
	 * @return
	 */
	public Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> setCellValue(int row, int col, int value) {
		// Set the value in the Sudoku board
		Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> setStatus = this.gameBoard.setCellValue(row, col, value);
		
		if (setStatus.getFirst() != SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS) {
			return setStatus;
		}
		
		// Clear the draft list
		this.clearCellDraft(row, col);
		
		return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>>(
				   SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS, new Pair<Integer, Integer>(row, col)); 
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 */
	public void unsetCellValue(int row, int col) {
		this.gameBoard.unsetCellValue(row, col);
	}
	
	/**
	 * Unsets cell value and clears cell draft list
	 * 
	 * @param row
	 * @param col
	 */
	public void clearCell(int row, int col) {
		this.unsetCellValue(row, col);
		this.clearCellDraft(row, col);
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @param draftNum
	 * @return
	 */
	public Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> addDraftNumber(int row, int col, int draftNum) {
		Pair<SudokuBoard.PLACE_NUMBER_STATUS, Pair<Integer, Integer>> placeStatus = 
				gameBoard.canSetValue(row, col, draftNum);
		if (placeStatus.getFirst() != SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS) {
			return placeStatus;
		}
		
		if (draftBoard.get(row).get(col).size() > 0) {
			// The draft list is non-empty. So add the new draft number if it does not exist yet
			if (!draftBoard.get(row).get(col).contains(draftNum)) {
				draftBoard.get(row).get(col).add(draftNum);
			}
		} else {
			// Draft list is empty. Check if the cell is empty as well
			if (this.gameBoard.isEmptyCell(row, col)) {
				// This cell is empty. So set the first draft number to be its value
				this.setCellValue(row, col, draftNum);
			} else {
				if (this.getCellValue(row, col) != draftNum) {
					// There are 2 draft numbers. Unset the cell value. Add two values to draft list
					Integer oldCellValue = this.getCellValue(row, col);
					this.unsetCellValue(row, col);
					draftBoard.get(row).get(col).add(draftNum);
					Log.i("add draft", "dkm");
					draftBoard.get(row).get(col).add(oldCellValue);
				}
			}
		}
		
		return placeStatus;
	}
	
	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 * 
	 * @param row
	 * @param col
	 */
	public void removeDraftNumber(int row, int col, int draftNum) {
		if (row < 0)
			throw new IndexOutOfBoundsException("Row index is negative");
		if (row >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Row index exceeds board size");
		if (col < 0)
			throw new IndexOutOfBoundsException("Column index is negative");
		if (col >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Column index exceeds board size");
		
		if (draftNum <= 0 || draftNum > gameBoard.getBoardSize()) {
			return;
		}
		
		// Check if this is the first draft number
		if (draftBoard.get(row).get(col).size() == 0 &&
			this.getCellValue(row, col) == draftNum) {
			// Unset the cell value
			this.unsetCellValue(row, col);
			return;
		}
		
		if (!draftBoard.get(row).get(col).contains(draftNum)) {
			return;
		}
		
		// draftNum is in the draft list for this cell
		draftBoard.get(row).get(col).remove(draftNum);
	}
	
	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 * 
	 * @param row
	 * @param col
	 */
	public void clearCellDraft(int row, int col) {
		if (row < 0)
			throw new IndexOutOfBoundsException("Row index is negative");
		if (row >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Row index exceeds board size");
		if (col < 0)
			throw new IndexOutOfBoundsException("Column index is negative");
		if (col >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Column index exceeds board size");
		
		int sizeDraftList = draftBoard.get(row).get(col).size();
		if (sizeDraftList == 1)
			gameBoard.unsetCellValue(row, col);
		draftBoard.get(row).get(col).clear();
	}
	
	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 *
	 * @param row
	 * @param col
	 * @return
	 */
	public List<Integer> getCellDraftList(int row, int col) {
		if (row < 0)
			throw new IndexOutOfBoundsException("Row index is negative");
		if (row >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Row index exceeds board size");
		if (col < 0)
			throw new IndexOutOfBoundsException("Column index is negative");
		if (col >= gameBoard.getBoardSize())
			throw new IndexOutOfBoundsException("Column index exceeds board size");
		
		return new Vector<Integer>(draftBoard.get(row).get(col));
	}
	
	/**
	 * 
	 * @return
	 */
	public SudokuBoard getSudokuBoard() {
		return gameBoard;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSolved() {
		return gameBoard.isSolved();
	}
}
