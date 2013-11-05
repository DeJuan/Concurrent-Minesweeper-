package ast;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent the board state for our implementation of Minesweeper.
 * It has a single representation invariant:
 * 1) Size must not be negative.
 * 
 *
 */
public class Board
{
	private final int size;
	private ArrayList<ArrayList<Square>> boardState;
	
	/*
	 * Default constructor: If size not specified, default to 10. 
	 */
	public Board()
	{
		this.size = 10;
		ArrayList<Square> row = new ArrayList<Square>();
		Square space = new Square();
		for(int i = 0; i < this.size; i++)
		{
			row.add(space);
		}
		this.boardState = new ArrayList<ArrayList<Square>>();
		for(int j = 0; j < this.size; j++)
		{
			this.boardState.add(row);
		}
	}
	/*
	 * If we do have a size we want, use it
	 */
	public Board(int size)
	{	
		if (size <= 0)
		{
			throw new IllegalArgumentException("The board cannot have a negative size!");
		}
		this.size = size;
		ArrayList<Square> row = new ArrayList<Square>();
		Square space = new Square();
		for(int i = 0; i < this.size; i++)
		{
			row.add(space);
		}
		this.boardState = new ArrayList<ArrayList<Square>>();
		for(int j = 0; j < this.size; j++)
		{
			this.boardState.add(row);
		}
	}
	
	/*
	 * Returns the board size.  We shouldn't ever change size once the board
	 * has been created, so it's a final.
	 */
	public final int getBoardSize()
	{
		return this.size;
	}
	
	/*
	 * Returns the state of the board. This returns the type of square that it is, i.e.
	 * "-", "F", etc.
	 */
	public String[][] getBoardState()
	{
		String[][] boardRep = new String[this.size][this.size];
		for(int row = 0; row < this.size; row++)
		{
			for(int col = 0; col < this.size; col++)
			{
				boardRep[row][col] = this.boardState.get(row).get(col).toString();
			}
		}
		return boardRep;
	}
}
