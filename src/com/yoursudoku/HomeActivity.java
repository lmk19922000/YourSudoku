package com.yoursudoku;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener{
	Button playButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.playButton:
			Intent myIntent = new Intent(HomeActivity.this, SudokuCanvasActivity.class);
			startActivity(myIntent);
			break;
		}
	}

}
