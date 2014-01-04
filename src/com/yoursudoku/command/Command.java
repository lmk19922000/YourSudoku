package com.yoursudoku.command;

public interface Command {
	public void execute();
	public void undo();
}
