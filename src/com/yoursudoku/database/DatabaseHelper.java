package com.yoursudoku.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "MainDatabase";
	private static final int VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE [sudoku_board] ("
				+ "[id] INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "[board_size] INTEGER, "
				+ "[sub_board_size] INTEGER, "
				+ "[board_data] TEXT, "
				+ "[num_empty] INTEGER, "
				+ "[difficulty_level] INTEGER, "
				+ "[max_point_earned] INTEGER, "
				+ "[map_unit_to_number_sieve] TEXT"
				+ ");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("VKL VKL VKL");
		
	}

}
