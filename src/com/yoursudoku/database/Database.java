package com.yoursudoku.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
	private static Database instance = null;
	
	private DatabaseHelper helper;
	private SQLiteDatabase db;
	
	// Singleton
	protected Database(){
		
	}
	
	public static Database getDatabase (){
		if (instance == null){
			instance = new Database();
		}
		
		return instance;
	}
	
	public String execute(Context c){
		helper = new DatabaseHelper(c);
		db = helper.getReadableDatabase();
		
		Cursor cursor;
		String sql;
		//sql = "INSERT INTO [sudoku_board] ([board_data]) VALUES ('DCM')";
		//db.execSQL(sql);
		//sql = "INSERT INTO [sudoku_board] ([board_size], [board_data]) VALUES (12, 'VKL')";
		//db.execSQL(sql);
		sql = "SELECT sudoku_board.board_data FROM sudoku_board";
		cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}
}
