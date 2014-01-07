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
	// Private Class Constants
	private final static String ROW_STR = "R";
	private final static String COL_STR = "C";
	private final static String SUB_SQUARE_STR = "S";
	private final static boolean NOT_EXISTED = false;
	private final static boolean EXISTED = true;
	private final static int DEFAULT_BOARD_SIZE = 9;
	
	// Public Class Constants
	public final static int EMPTY_VALUE = 0;
	public enum PLACE_NUMBER_STATUS {
		SUCCESS, ROW_CONFLICT, COL_CONFLICT, SUB_SQUARE_CONFLICT, INVALID_VALUE, INVALID_ROW_COL
	};
	
	// Class attributes
	private int boardSize;
	private int subBoardSize;
	private Integer[][] boardData;
	private int numEmpty;
	private int difficultyLevel;
	private int maxPointEarned;
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
			throw new IllegalArgumentException("Board size must be a square number");
		
		constructEmptySudokuBoard(inputBoardSize, 0, 0);		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public SudokuBoard(SudokuBoard another) {
		difficultyLevel = another.difficultyLevel;
		maxPointEarned = another.maxPointEarned;
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
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @param inputBoardData
	 * @param difficultyLevel
	 * @param maxPointEarned
	 */
	public SudokuBoard(List<List<Integer>> inputBoardData, int difficultyLevel, int maxPointEarned) {
		int numRow = inputBoardData.size();
		if (numRow < 1)
			throw new IllegalArgumentException("The number of rows in the board must be at least 1");
		int numCol = inputBoardData.get(0).size();
		if (numRow != numCol)
			throw new IllegalArgumentException("The board must be a square board");
		if (!Utility.isSquareNumber(numRow))
			throw new IllegalArgumentException("Board size must be a square number");
		
		constructEmptySudokuBoard(numRow, difficultyLevel, maxPointEarned);
		boolean success = setSudokuBoardData(inputBoardData);
		if (!success)
			throw new IllegalArgumentException("There is a conflict in the input Sudoku Board data");
	}

	/**
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @param inputBoardData
	 */
	public SudokuBoard(List<List<Integer>> inputBoardData) {
		this(inputBoardData, 0, 0);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @param inputBoardData
	 * @param difficultyLevel
	 * @param maxPointEarned
	 */
	public SudokuBoard(Integer[][] inputBoardData, int difficultyLevel, int maxPointEarned) {
		int numRow = inputBoardData.length;
		if (numRow < 1)
			throw new IllegalArgumentException("The number of rows in the board must be at least 1");
		int numCol = inputBoardData[0].length;
		if (numRow != numCol)
			throw new IllegalArgumentException("The board must be a square board");
		if (!Utility.isSquareNumber(numRow))
			throw new IllegalArgumentException("Board size must be a square number");
		
		constructEmptySudokuBoard(numRow, difficultyLevel, maxPointEarned);
		
		List<List<Integer>> formatBoardData = new ArrayList<List<Integer>>();
		for (int row = 0; row < numRow; row++) {
			formatBoardData.add(Arrays.asList(inputBoardData[row]));
		}
		boolean success = setSudokuBoardData(formatBoardData);
		if (!success)
			throw new IllegalArgumentException("There is a conflict in the input Sudoku Board data");
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @param inputBoardData
	 */
	public SudokuBoard(Integer[][] inputBoardData) {
		this(inputBoardData, 0, 0);
	}
	
	/**
	 * 
	 * @param inputBoardSize
	 */
	private void constructEmptySudokuBoard(int inputBoardSize, int inputDifficultyLevel, int inputMaxPointEarned) {
		difficultyLevel = inputDifficultyLevel;
		maxPointEarned = inputMaxPointEarned;
		boardSize = inputBoardSize;
		subBoardSize = (int) Math.round(Math.floor(Math.sqrt((double) inputBoardSize)));
		numEmpty = boardSize * boardSize;
		boardData = new Integer[boardSize][boardSize];
		for (int r = 0; r < boardSize; r++)
			for (int c = 0; c < boardSize; c++)
				boardData[r][c] = EMPTY_VALUE;
		
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
	 * 
	 * @param inputBoardData
	 * @return
	 */
	private boolean setSudokuBoardData(List<List<Integer>> inputBoardData) {
		for (int row = 0; row < boardSize; row++) {
			String rowKey = ROW_STR + Integer.toString(row);
			for (int col = 0; col < boardSize; col++) {
				Integer val = inputBoardData.get(row).get(col);
				if (val == null || val <= 0 || val > boardSize)
					boardData[row][col] = EMPTY_VALUE;
				else {
					boardData[row][col] = val;
					int valInd = val - 1;
					
					String colKey = COL_STR + Integer.toString(col);
					String subSquareKey = SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col));
					if (mapUnitToNumberSieve.get(rowKey).get(valInd) == EXISTED ||
						mapUnitToNumberSieve.get(colKey).get(valInd) == EXISTED ||
						mapUnitToNumberSieve.get(subSquareKey).get(valInd) == EXISTED)
						return false;
					mapUnitToNumberSieve.get(rowKey).set(valInd,  EXISTED);
					mapUnitToNumberSieve.get(colKey).set(valInd, EXISTED);
					mapUnitToNumberSieve.get(subSquareKey).set(valInd, EXISTED);
				}
			}
		}
		
		return true;
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
		return boardData[row][col] == EMPTY_VALUE;
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
	public int getDifficultyLevel() {
		return difficultyLevel;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxPointEarned() {
		return maxPointEarned;
	}
	
	/**
	 * 
	 * @param difficultyLevel
	 */
	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}
	
	/**
	 * 
	 * @param maxPointEarned
	 */
	public void setMaxPointEarned(int maxPointEarned) {
		this.maxPointEarned = maxPointEarned;
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
				if (boardData[row][col] != EMPTY_VALUE)
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
	public Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer> canSetValue(int row, int col, int value) {
		// Handle invalid-input cases
		if (row < 0 || col < 0 || row >= boardSize || col >= boardSize)
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.INVALID_VALUE, -1);
		if (value <= 0 || value > boardSize)
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.INVALID_ROW_COL, -1);
		
		// Handle normal cases
		if (value == boardData[row][col])
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS, -1);
		int valueInd = value - 1;
		if (mapUnitToNumberSieve.get(ROW_STR + Integer.toString(row)).get(valueInd) == EXISTED)
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.ROW_CONFLICT, row);
		if (mapUnitToNumberSieve.get(COL_STR + Integer.toString(col)).get(valueInd) == EXISTED)
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.COL_CONFLICT, col);
		if (mapUnitToNumberSieve.get(SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col))).get(valueInd) == EXISTED)
			return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.SUB_SQUARE_CONFLICT, getSubBoardId(row, col));
		return new Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer>(SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS, -1);
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @param value
	 * @return
	 */
	public Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer> setCellValue(int row, int col, int value) {
		Pair<SudokuBoard.PLACE_NUMBER_STATUS, Integer> placeResult = canSetValue(row, col, value);
		if (placeResult.getFirst() != SudokuBoard.PLACE_NUMBER_STATUS.SUCCESS)
			return placeResult;
		
		if (!isEmptyCell(row, col))
			unsetCellValue(row, col);
		
		numEmpty--;
		boardData[row][col] = value;
		int valueInd = value - 1;
		mapUnitToNumberSieve.get(ROW_STR + Integer.toString(row)).set(valueInd, EXISTED);
		mapUnitToNumberSieve.get(COL_STR + Integer.toString(col)).set(valueInd, EXISTED);
		mapUnitToNumberSieve.get(SUB_SQUARE_STR + Integer.toString(getSubBoardId(row, col))).set(valueInd, EXISTED);
		return placeResult;
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
		boardData[row][col] = EMPTY_VALUE;
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
