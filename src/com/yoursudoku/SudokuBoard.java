/**
 * Implements a class representing a Sudoku board.
 * 
 * @author truongduy134@gmail.com (Nguyen Truong Duy)
 */
package com.yoursudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SudokuBoard {
	// Class constants
	private final static String ROW_STR = "R";
	private final static String COL_STR = "C";
	private final static String SUB_SQUARE_STR = "S";
	private final static boolean NOT_EXISTED = false;
	private final static boolean EXISTED = true;
	private final static int DEFAULT_BOARD_SIZE = 9;
	
	// Class attributes
	private int boardSize;
	private int subBoardSize;
	private Integer[][] boardData;
	private int numEmpty;
	private HashMap<String, Vector<Boolean>> mapUnitToNumberSieve;
	
	/**
	 * Default constructor: Creates a standard 9 x 9 Sudoku board
	 */
	public SudokuBoard() {
		this(DEFAULT_BOARD_SIZE);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException
	 *
	 * @param inputBoardSize
	 */
	public SudokuBoard(int inputBoardSize) {
		if (!Utility.isSquareNumber(inputBoardSize))
			throw new IllegalArgumentException("Board size must be square");
		
		boardSize = inputBoardSize;
		subBoardSize = (int) Math.round(Math.floor(Math.sqrt((double) inputBoardSize)));
		numEmpty = boardSize * boardSize;
		boardData = new Integer[boardSize][boardSize];
		for (int r = 0; r < boardSize; r++)
			for (int c = 0; c < boardSize; c++)
				boardData[r][c] = null;
		
		Vector<Boolean> sampleNumberSieve = new Vector<Boolean>();
		for (int numEle = 0; numEle < boardSize; numEle++)
			sampleNumberSieve.add(NOT_EXISTED);
		for (int id = 0; id < boardSize; id++) {
			mapUnitToNumberSieve.put(ROW_STR + Integer.toString(id), new Vector<Boolean>(sampleNumberSieve));
			mapUnitToNumberSieve.put(COL_STR + Integer.toString(id), new Vector<Boolean>(sampleNumberSieve));
			mapUnitToNumberSieve.put(SUB_SQUARE_STR + Integer.toString(id), new Vector<Boolean>(sampleNumberSieve));
		}		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public SudokuBoard(SudokuBoard another) {
		boardSize = another.boardSize;
		subBoardSize = another.subBoardSize;
		numEmpty = another.numEmpty;
		
		boardData = new Integer[boardSize][boardSize];
		for (int row = 0; row < boardSize; row++)
			for (int col = 0; col < boardSize; col++)
				boardData[row][col] = another.boardData[row][col];
		
		mapUnitToNumberSieve = new HashMap<String, Vector<Boolean>>();
		for (String key : another.mapUnitToNumberSieve.keySet()) {
			Vector<Boolean> sieveData = new Vector<Boolean>(another.mapUnitToNumberSieve.get(key));
			mapUnitToNumberSieve.put(key, sieveData);
		}
	}
	
	/**
	 * @throws IndexOutOfBoundsException
	 */
	public boolean isEmptyCell(int row, int col) {
		if (row < 0)
			throw new IndexOutOfBoundsException("Row index is negative");
		if (row >= boardSize)
			throw new IndexOutOfBoundsException("Row index exceeds board size");
		if (col < 0)
			throw new IndexOutOfBoundsException("Column index is negative");
		if (col >= boardSize)
			throw new IndexOutOfBoundsException("Column index exceeds board size");
		return boardData[row][col] == null;
	}
	
	/**
	 *  
	 * @return
	 */
	public int getNumEmptyCells() {
		return numEmpty;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSolved() {
		return numEmpty == 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getBoardSize() {
		return boardSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSubBoardSize() {
		return subBoardSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<List<Integer>> getSudokuBoardData() {
		List<List<Integer>> retData = new ArrayList<List<Integer>>();
		
		for (int row = 0; row < boardSize; row++)
			retData.add(new ArrayList<Integer>(Arrays.asList(boardData[row])));
		
		return retData;
	}
	
	public List<Pair<Integer, Integer>> getNonEmptyCells() {
		List<Pair<Integer, Integer>> nonEmptyCellList = new ArrayList<Pair<Integer, Integer>>();
		
		for (int row = 0; row < boardSize; row++)
			for (int col = 0; col < boardSize; col++)
				if (boardData[row][col] != null)
					nonEmptyCellList.add(new Pair<Integer, Integer>(row, col));
		
		return nonEmptyCellList;
	}
	
	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param row
	 * @param col
	 * @return
	 */
	public Integer getCellValue(int row, int col) {
		if (row < 0)
			throw new IndexOutOfBoundsException("Row index is negative");
		if (row >= boardSize)
			throw new IndexOutOfBoundsException("Row index exceeds board size");
		if (col < 0)
			throw new IndexOutOfBoundsException("Column index is negative");
		if (col >= boardSize)
			throw new IndexOutOfBoundsException("Column index exceeds board size");
		return boardData[row][col];
	}
	
	/**
	 *  
	 * @param row
	 * @param col
	 * @param value
	 * @return
	 */
	public boolean canSetValue(int row, int col, int value) {
		// Handle invalid-input cases
		if (row < 0 || col < 0 || row >= boardSize || col >= boardSize)
			return false;
		if (value <= 0 || value > boardSize)
			return false;
		
		// Handle normal cases
		if (value == boardData[row][col])
			return true;
		int valueInd = value - 1;
		if (mapUnitToNumberSieve.get(ROW_STR + Integer.toString(row)).get(valueInd) == EXISTED ||
			mapUnitToNumberSieve.get(COL_STR + Integer.toString(col)).get(valueInd) == EXISTED ||
			mapUnitToNumberSieve.get(SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col))).get(valueInd) == EXISTED)
			return false;
		return true;
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @param value
	 * @return
	 */
	public boolean setCellValue(int row, int col, int value) {
		if (!canSetValue(row, col, value))
			return false;
		
		if (!isEmptyCell(row, col))
			unsetCellValue(row, col);
		
		numEmpty--;
		boardData[row][col] = value;
		int valueInd = value - 1;
		mapUnitToNumberSieve.get(ROW_STR + Integer.toString(row)).set(valueInd, EXISTED);
		mapUnitToNumberSieve.get(COL_STR + Integer.toString(col)).set(valueInd, EXISTED);
		mapUnitToNumberSieve.get(SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col))).set(valueInd, EXISTED);
		return true;
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 */
	public void unsetCellValue(int row, int col) {
		if (row < 0 || col < 0 || row >= boardSize || col >= boardSize)
			return;
		
		if (isEmptyCell(row, col))
			return;
		
		numEmpty++;
		int oldVal = boardData[row][col];
		boardData[row][col] = null;
		int oldValInd = oldVal - 1;
		mapUnitToNumberSieve.get(ROW_STR + Integer.toString(row)).set(oldValInd, NOT_EXISTED);
		mapUnitToNumberSieve.get(COL_STR + Integer.toString(col)).set(oldValInd, NOT_EXISTED);
		mapUnitToNumberSieve.get(SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col))).set(oldValInd, NOT_EXISTED);
	}
	
	/**
	 * Returns the id of the sub-board that the input cell belongs to
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private int getSubBoardId(int row, int col) {
		int rowGroupInd = row / subBoardSize;
		int colGroupInd = row / subBoardSize;
		int numSubBoardPerRow = subBoardSize;
		return rowGroupInd * numSubBoardPerRow + colGroupInd;
	}	
}
