package com.yoursudoku;

import java.util.List;
import java.util.Vector;

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
	 * @param draftNum
	 * @return
	 */
	public Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer> addDraftNumber(int row, int col, int draftNum) {
		Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer> placeStatus = gameBoard.canSetValue(row, col, draftNum);
		if (placeStatus.getFirst() != SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS)
			return placeStatus;
		
		if (draftBoard.get(row).get(col).contains(draftNum))
			return placeStatus;
		
		if (draftBoard.get(row).get(col).size() == 0) {
			gameBoard.setCellValue(row, col, draftNum);
		} else {
			if (draftBoard.get(row).get(col).size() == 1)
				gameBoard.unsetCellValue(row, col);
		}
		draftBoard.get(row).get(col).add(draftNum);
		
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
		
		if (draftNum <= 0 || draftNum > gameBoard.getBoardSize())
			return;
		if (!draftBoard.get(row).get(col).contains(draftNum))
			return;
		
		// draftNum is in the draft list for this cell
		if (draftBoard.get(row).get(col).size() == 1)
			gameBoard.unsetCellValue(row, col);
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
	public boolean isSolved() {
		return gameBoard.isSolved();
	}
}
