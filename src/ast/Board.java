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
		
		this.boardState = new ArrayList<ArrayList<Square>>();
		for(int j = 0; j < this.size; j++)
		{
			this.boardState.add(createRow());
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
		for(int i = 0; i < this.size; i++)
		{
			row.add(new Square());
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
	
	public String toString()
	{
		StringBuilder boardString = new StringBuilder(this.size*this.size + this.size);
		for(int row = 0; row < this.size; row++)
		{
			for(int col = 0; col < this.size; col++)
			{
				boardString.append(this.boardState.get(row).get(col).toString());
			}
			boardString.append("\n");
		}
		return boardString.toString();
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
	
	public ArrayList<ArrayList<Square>> getActualBoardStateForDebugPurposes()
	{
		return this.boardState;
	}
	/*
	 * User-to-Server Minesweeper Message Protocol
  MESSAGE     :== ( LOOK | DIG | FLAG | DEFLAG | HELP_REQ | BYE ) NEWLINE
  LOOK        :== "look"
  DIG         :== "dig" SPACE X SPACE Y
  FLAG        :== "flag" SPACE X SPACE Y
  DEFLAG      :== "deflag" SPACE X SPACE Y
  HELP_REQ    :== "help"
  BYE         :== "bye"
  NEWLINE     :== "\r?\n"
  X           :== INT
  Y           :== INT
  SPACE       :== " "
  INT         :== [0-9]+
	 */
	public String processLook()
	{
		return this.toString();
	}
	
	public String processFlag(String input)
	{
		int X = Integer.valueOf(input.substring(5, 6));
		int Y = Integer.valueOf(input.substring(7, 8));
		boardState.get(X).get(Y).setStatus("F");
		return this.toString();
	}
	
	public String processDeflag(String input)//string is "deflag_X_Y"
	{
		int X = Integer.valueOf(input.substring(7,8));
		int Y = Integer.valueOf(input.substring(9,10));
		Square squareRequested = boardState.get(X).get(Y);
		if (squareRequested.getDescription() == "flagged"){
				squareRequested.setStatus("-");
		}
		return this.toString();
	}
	public String processDig(String input)
	{
		//Input is dig_X_Y
		int locationDataX = Integer.valueOf(input.substring(4,5));
		int locationDataY = Integer.valueOf(input.substring(6,7));
		if(locationDataX < 0 || locationDataX > this.size || locationDataY < 0 || locationDataY > this.size)
		{
			return processLook();
		}
		//At this point, we know the square indicated exists, so this next line is okay to do:
		Square requestedSquare = boardState.get(locationDataX).get(locationDataY); 
		if(requestedSquare.getDescription() != "untouched")
		{
			return this.toString();
		}
		
		if(requestedSquare.getDescription() == "bomb")
		{
			requestedSquare.setStatus(" ");
			int bombCount = 0;
			ArrayList<Square> adjacentToThisSquare = adjacentSquares(locationDataX,locationDataY);
			ArrayList<Square> squaresAdjacentToSquaresAdjacentToThisSquare;
			for (Square s: adjacentToThisSquare)
			{
				
			}
			
		}
		return this.toString();
	}
	
	public ArrayList<Square> adjacentSquares(int locationDataX, int locationDataY)
	{
		ArrayList<Square> adjacencyList = new ArrayList<Square>();
		int rightOne = locationDataX+1;
		int leftOne = locationDataX+1;
		int upOne = locationDataY-1;
		int downOne = locationDataY+1;
		Square Northwest;
		Square North;
		Square Northeast;
		Square West;
		Square East;
		Square Southwest;
		Square South;
		Square Southeast;
		boolean upValid = true;
		boolean downValid = true;
		if (upOne < 0)
		{ 
			upValid = false;
		}
		if (downOne < this.size)
		{
			downValid = false;
		}
		//Seems like there's no way around indexing  through the squares. 
		//Check if they're valid first.
		if(rightOne < this.size)
		{
			
			East = boardState.get(rightOne).get(locationDataY);
			adjacencyList.add(East);
			
			if(upValid)
			{
				Northeast = boardState.get(rightOne).get(upOne);
				adjacencyList.add(Northeast);
			}
			
			if(downValid)
			{
				Southeast = boardState.get(rightOne).get(downOne);
				adjacencyList.add(Southeast);
			}
		}
		
		if(upValid)
		{
			North = boardState.get(locationDataX).get(upOne);
			adjacencyList.add(North);
		}
		
		if(downValid)
		{
			South = boardState.get(locationDataX).get(downOne);
			adjacencyList.add(South);
		}
		
		if(leftOne > 0)
		{
			
			West = boardState.get(leftOne).get(locationDataY);
			adjacencyList.add(West);
			
			if(upValid)
			{
				Northwest = boardState.get(leftOne).get(upOne);
				adjacencyList.add(Northwest);
			}
			
			if(downValid)
			{
				Southwest = boardState.get(leftOne).get(downOne);
				adjacencyList.add(Southwest);
				
			}
		}
		
		return adjacencyList;
		/*
		 * That annoying iterative process we just did needs to be repeated, but slightly different. 
		 * We should abstract these away into other methods.
		 */
		
		
	}
	
	public ArrayList<Square> createRow()
	{
		ArrayList<Square> row = new ArrayList<Square>();
		for(int i = 0; i < this.size; i++)
		{
			row.add(new Square());
		}
		return row;
	}
}
