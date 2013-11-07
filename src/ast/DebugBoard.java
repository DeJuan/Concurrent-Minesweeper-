package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class is used to represent the board state for our implementation of Minesweeper.
 * It has a single representation invariant:
 * 1) Size must not be negative.
 * 
 *
 */
public class DebugBoard
{
	private final int size;
	private ArrayList<ArrayList<DebugSquare>> boardState;
	private int rowCounter = 0;
	
	/*
	 * Default constructor: If size not specified, default to 10. 
	 */
	
	public DebugBoard(){
		this.size = 10;
		this.boardState = new ArrayList<ArrayList<DebugSquare>>();
		for(int r = 0; r < this.size; r++)
		{
			this.boardState.add(createRow());
		}
		setAllCounts();
	}
	
	/*
	 * If we do have a size we want, use it
	 */
	
	public DebugBoard(int size)
	{	
		if (size <= 0)
		{
			throw new IllegalArgumentException("The board cannot have a negative size!");
		}
		this.size = size;
		this.boardState = new ArrayList<ArrayList<DebugSquare>>();
		for(int r = 0; r < this.size; r++)
		{
			this.boardState.add(createRow());
		}
		setAllCounts();
	}
	
	
	/*
	 * Returns the board size.  We shouldn't ever change size once the board
	 * has been created, so it's a final.
	 */
	public int getBoardSize()
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
	 * Returns the state of the board. This returns the type of DebugSquare that it is, i.e.
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
	
	public ArrayList<ArrayList<DebugSquare>> getActualBoardStateForDebugPurposes()
	{
		return this.boardState;
	}
	
	public void setAllCounts()
	{
		Queue<DebugSquare> squareQueue = new LinkedBlockingQueue<DebugSquare>();
		ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
		squareQueue.add(boardState.get(0).get(0));
		//System.out.println(squareQueue);
		while (!squareQueue.isEmpty())
		{
			int bombsFound = 0;
			DebugSquare currentSquare = squareQueue.poll();
			//System.out.println("currentDebugSquare is: " + currentSquare.getLocation().toString() + " = " + currentSquare.toString() );
			visited.add(currentSquare.getLocation());
			//System.out.println("visited is" + visited);
			int x = (int) currentSquare.getLocation().get(0);
			int y = (int) currentSquare.getLocation().get(1);
			ArrayList<DebugSquare> currentAdj = adjacentSquares(x,y);
			//System.out.print("adj currently contains: ");
			//for (DebugSquare m: currentAdj){
				//System.out.print(m.getLocation());
				
			//}
			//System.out.print("\n");
			for(DebugSquare s: currentAdj)
			{
				
				if (s.getDescription() == "bomb")
				{
					bombsFound += 1;
				}
				
				if (!visited.contains(s.getLocation()))
				{
					//System.out.println("Adding" + s.getLocation() + " to the queue");
					squareQueue.add(s);
					visited.add(s.getLocation());
				}
			}
			currentSquare.setCount(bombsFound);
		}
			
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
		DebugSquare squareRequested = boardState.get(X).get(Y);
		if (squareRequested.getDescription() == "flagged"){
				squareRequested.setStatus("-");
				
		}
		return this.toString();
	}
	
	public void processHelp()
	{
		  System.out.println("Valid Commands are: (LOOK :== \"look\"  | DIG :== \"dig\" SPACE X SPACE Y  | FLAG  :== \"flag\" SPACE X SPACE Y | DEFLAG :== \"deflag\" SPACE X SPACE Y | HELP_REQ :== \"help\" | BYE :== \"bye\" ) NEWLINE. X and Y are ints.");				 
	}
	public String processDig(String input)
	{
		//Input is dig_X_Y
		int locationDataX = Integer.valueOf(input.substring(4,5));
		int locationDataY = Integer.valueOf(input.substring(6,7));
		System.out.println( "Location in question is (" + locationDataX + "," + locationDataY + ")" );
		if(locationDataX < 0 || locationDataX > this.size || locationDataY < 0 || locationDataY > this.size)
		{
			return processLook();
		}
		//At this point, we know the DebugSquare indicated exists, so this next line is okay to do:
		DebugSquare requestedSquare = boardState.get(locationDataX).get(locationDataY);
		
		//If this is true, we've already dug it or flagged it so leave it be & return current state. 
		if(requestedSquare.getDescription() != "untouched")
		{
			return this.toString();
		}
		
		//Hard section. What to do if we get a bomb. 
		if(requestedSquare.getDescription() == "bomb")
		{
			requestedSquare.setStatus(" "); //Clear the bomb away.
		
			//Compose an array list containing all squares adjacent to this one. 
			ArrayList<DebugSquare> adjacentToThisSquare = adjacentSquares(locationDataX,locationDataY);
			
			//We want to examine every DebugSquare around these adjacent squares, and bombcount for all of them.
			for (DebugSquare s: adjacentToThisSquare)
			{
				int bombsFound = 0; //Number of adjacent bombs
				int xData = (int) s.getLocation().get(0); //This square's X location
				int yData = (int) s.getLocation().get(1); //This squares Y location
				ArrayList<DebugSquare> squaresAdjacentToSquaresAdjacentToThisSquare = adjacentSquares(xData,yData); //Use it to call adjacency again
				for (DebugSquare newLevel : squaresAdjacentToSquaresAdjacentToThisSquare) //For every DebugSquare we just located:
				{
					if (newLevel.getStatus() == "bomb") //If that DebugSquare is a bomb:
					{
						bombsFound +=1; //Increment our bombsFound counter.
					}
				}
				s.setCount(bombsFound); //Lastly, set the count of that DebugSquare to bombsFound and restart, reinitializing everything for the next square.
			}
			return "BOOM!";
		}
		if (requestedSquare.getDescription() == "untouched"){
			System.out.println("Hit the untouched message");
			requestedSquare.setStatus(" "); // change to dug
			ArrayList<DebugSquare> adjacents = adjacentSquares(locationDataX, locationDataY);
			Queue<DebugSquare> squareQueue = new LinkedBlockingQueue<DebugSquare>();
			ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
			squareQueue.addAll(adjacents);
			while (!squareQueue.isEmpty()){
				DebugSquare s = squareQueue.poll();
				visited.add(s.getLocation());
				System.out.println("currentSquare is: " + s.getLocation().toString() + " = " + s.toString() );
				if (s.getDescription() != "bomb")
				{
					s.setStatus(" ");
					int xLoc = (int) s.getLocation().get(0);
					int yLoc = (int) s.getLocation().get(1);
					ArrayList<DebugSquare> prospects = (adjacentSquares(xLoc,yLoc));
					for (DebugSquare m: prospects)
					{
						System.out.println("Current prospect is" + m.getLocation());
						if(!visited.contains(m.getLocation()))
						{
							squareQueue.add(m);
							//System.out.println("Adding " + m.getLocation() + "to queue and visited.");
							visited.add(m.getLocation());
						}
					}
					
				}
			}
			return this.toString();
		}
		return this.toString();
	}
	
	
	public ArrayList<DebugSquare> adjacentSquares(int locationDataX, int locationDataY)
	{
		ArrayList<DebugSquare> adjacencyList = new ArrayList<DebugSquare>();
		int rightOne = locationDataX+1;
		int leftOne = locationDataX-1;
		int upOne = locationDataY-1;
		int downOne = locationDataY+1;
		DebugSquare Northwest;
		DebugSquare North;
		DebugSquare Northeast;
		DebugSquare West;
		DebugSquare East;
		DebugSquare Southwest;
		DebugSquare South;
		DebugSquare Southeast;
		boolean upValid = true;
		boolean downValid = true;
		if (upOne < 0)
		{ 
			upValid = false;
		}
		if (downOne >= this.size)
		{
			downValid = false;
		}
		//Seems like there's no way around indexing  through the squares. 
		//Check if they're valid first.
		if(rightOne < this.size)
		{
			
			East = boardState.get(rightOne).get(locationDataY);
			//System.out.println("Adding East, located at (" + rightOne +"," + locationDataY + ")");
			adjacencyList.add(East);
			
			if(upValid)
			{
				Northeast = boardState.get(rightOne).get(upOne);
				//System.out.println("Adding NorthEast, located at (" + rightOne +"," + upOne + ")");
				adjacencyList.add(Northeast);
			}
			
			if(downValid)
			{
				Southeast = boardState.get(rightOne).get(downOne);
				//System.out.println("Adding Southeast, located at (" + rightOne +"," + downOne + ")");
				adjacencyList.add(Southeast);
			}
		}
		
		if(upValid)
		{
			North = boardState.get(locationDataX).get(upOne);
			//System.out.println("Adding North, located at (" + locationDataX +"," + upOne + ")");
			adjacencyList.add(North);
		}
		
		if(downValid)
		{
			South = boardState.get(locationDataX).get(downOne);
			//System.out.println("Adding South, located at (" + locationDataX +"," + downOne + ")");
			adjacencyList.add(South);
		}
		
		if(leftOne >= 0)
		{
			
			West = boardState.get(leftOne).get(locationDataY);
			//System.out.println("Adding west, located at (" + leftOne +"," + locationDataY + ")");
			adjacencyList.add(West);
			
			if(upValid)
			{
				Northwest = boardState.get(leftOne).get(upOne);
				//System.out.println("Adding Northwest, located at (" + leftOne +"," + upOne + ")");
				adjacencyList.add(Northwest);
			}
			
			if(downValid)
			{
				Southwest = boardState.get(leftOne).get(downOne);
				//System.out.println("Adding Southwest, located at (" + leftOne +"," + downOne + ")");
				adjacencyList.add(Southwest);
				
			}
		}
		
		return adjacencyList;
		/*
		 * That annoying iterative process we just did needs to be repeated, but slightly different. 
		 * We should abstract these away into other methods.
		 */
		
		
	}
	
	public ArrayList<DebugSquare> createRow()
	{
		
		ArrayList<DebugSquare> row = new ArrayList<DebugSquare>();
		for(int j = 0; j < this.size; j++)
		{
			row.add(new DebugSquare(this.rowCounter, j));
		}
		this.rowCounter +=1;
		return row;
	}
}
