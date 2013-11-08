package ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class is used to represent the board state for our implementation of Minesweeper.
 * It has two representation invariants:
 * 1) Size must not be negative.
 * 2) The boards are square.
 * 2) Two threads should not concurrently do anything to modify the board. 
 *
 *The first is explicitly enforced.
 *The second is ensured by making all methods which modify the board synchronized.
 *In other words, Board utilizes the monitor pattern.
 *All players are sharing the same board, so acquiring the lock on the board will prevent
 * any other synchonized method from being carried out at the same time since there's only one 
 * "this". The methods that aren't synchronized don't need to be, as the players have no way to access
 * those methods, and even if they did, using them concurrently would be fine, with the exception
 * of the constructor, but there's only one board at any time so that's not an issue. 
 */
public class Board
{
	private final int size;
	private ArrayList<ArrayList<Square>> boardState;
	private int rowCounter = 0;
	
	public void checkRep(){
		assert(this.size >= 2);
		assert(boardState.get(0).size() == boardState.get(1).size());
	}
	/**
	 * Default constructor: If size not specified, creates a Board instance of size 10x10. 
	 */
	
	public Board(){
		this.size = 10;
		this.boardState = new ArrayList<ArrayList<Square>>();
		for(int r = 0; r < this.size; r++)
		{
			this.boardState.add(createRow());
		}
		setAllCounts();
		checkRep();
	}
	
	/**
	 * If we do have a size we want, use it to construct the board.
	 * @param int size, used to initialize board of sizexsize squares
	 */
	
	public Board(int size)
	{	
		if (size <= 1)
		{
			throw new IllegalArgumentException("The board cannot have that size!");
		}
		this.size = size;
		this.boardState = new ArrayList<ArrayList<Square>>();
		for(int r = 0; r < this.size; r++)
		{
			this.boardState.add(createRow());
		}
		setAllCounts();
		checkRep();
	}
	
	/**
	 * Lastly, if we have a particular file we wish to read from disk, we can load that to make our board.
	 */
	public Board(File file)
	{
		int count = 0;
		String output = "";
		ArrayList<Square> row = new ArrayList<Square>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {
            String currentLine;
            while ((currentLine = br.readLine()) != null) 
            {
            	output += (currentLine + "\r\n");
            	for (int index = 0; index < output.length()-1; index++)
            	{
            		row.add(new Square(count, index, output.charAt(index)));
            	}
            	this.boardState.add(row);
            	row.clear();
                count +=1;
                output = "";
            }
            
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        this.size = count;
	}
	
	
	/**
	 * Returns the board size.  We never change size once the board
	 * has been created. so size is a final variable.
	 */
	public int getBoardSize()
	{
		return this.size;
	}
	
	/**
	 * This method overrides the normal toString for any Object. It creates a StringBuilder object,
	 * and iterates through each square in the board, collecting the output of that square's 
	 * toString() method. It adds newlines to itself for each row completed.
	 * 
	 * Once the last square has been visited and its response recorded, we return the StringBuilder's
	 * accumulated String.
	 * 
	 *  @returns A "grid" string representation of the current board as a player should see it.
	 */
	@Override
	public synchronized String toString()
	{
		StringBuilder boardString = new StringBuilder(this.size*this.size + this.size*2);
		for(int row = 0; row < this.size; row++)
		{
			for(int col = 0; col < this.size; col++)
			{
				boardString.append(this.boardState.get(col).get(row).toString());
			}
			boardString.append("\r\n");
		}
		return boardString.toString();
	}
	/**
	 * Returns the state of the board as a String[][] rather than a normal String. 
	 * This allows checking for and returning  the type of square that a space is, i.e.
	 * "-", "F", etc.
	 */
	
	public synchronized String[][] getBoardState()
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
	/**
	 * This method is only used in debugging: it returns the actual arrayList we use to store
	 * all the board information. This is direct rep exposure and will be removed before final release,
	 * and the codes used to test it in BoardTest commented out. 
	 * @return ArrayList<ArrayList<Square>> which is the board itself.
	 */
	public ArrayList<ArrayList<Square>> getActualBoardStateForDebugPurposes()
	{
		return this.boardState;
	}
	
	/**
	 * This method is only called in the constructor right after we first initialize the board. It executes a 
	 * BFS starting from the upper left corner of the board, which is position (0,0), 
	 * and sets the counts for every square in the board as the search expands. 
	 */
	public void setAllCounts()
	{
		Queue<Square> squareQueue = new LinkedBlockingQueue<Square>();
		ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
		squareQueue.add(boardState.get(0).get(0));
		//System.out.println(squareQueue);
		while (!squareQueue.isEmpty())
		{
			int bombsFound = 0;
			Square currentSquare = squareQueue.poll();
			//System.out.println("currentSquare is: " + currentSquare.getLocation().toString() + " = " + currentSquare.toString() );
			visited.add(currentSquare.getLocation());
			//System.out.println("visited is" + visited);
			int x = (int) currentSquare.getLocation().get(0);
			int y = (int) currentSquare.getLocation().get(1);
			ArrayList<Square> currentAdj = adjacentSquares(x,y);
			//System.out.print("adj currently contains: ");
			//for (Square m: currentAdj){
				//System.out.print(m.getLocation());
				
			//}
			//System.out.print("\n");
			for(Square s: currentAdj)
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
	/**
	 * This method returns the current string representation of the board, as a player should see it.
	 * @return String indicating the current Board state to a player.
	 */
	public synchronized String processLook()
	{
		return this.toString();
	}
	
	/**
	 * This method allows a player to mark a square as flagged.
	 * No actions except for unflagging are available to a flagged square.
	 * @param input: A string in the format "flag X Y" 
	 * @return String representation of the currentBoard state with the updated Flag.
	 */
	public synchronized String processFlag(String input)
	{
		int X = Integer.valueOf(input.substring(5, 6));
		int Y = Integer.valueOf(input.substring(7, 8));
		if(X < 0 || X >= this.size || Y < 0 || Y >= this.size)
		{
			return this.toString();
		}
		System.out.println(X + "," + Y);
		boardState.get(Y).get(X).setStatus("F");
		return this.toString();
	}
	
	/**
	 * This method is the only way to return a flagged square to normal.
	 * It simply removes the flag if there is one, or does nothing otherwise.
	 * Then it prints the (potentially updated) state of the board.
	 * @param input: String in the format "deflag X Y" 
	 * @return String: representation of the board state after execution of deflag
	 */
	public synchronized String processDeflag(String input)//string is "deflag_X_Y"
	{
		int X = Integer.valueOf(input.substring(7,8));
		int Y = Integer.valueOf(input.substring(9,10));
		if(X < 0 || X >= this.size || Y < 0 || Y >= this.size)
		{
			return this.toString();
		}
		Square squareRequested = boardState.get(X).get(Y);
		if (squareRequested.getStatus() == "F")
		{
				squareRequested.setStatus("-");		
		}
		return this.toString();
	}
	/**
	 * This method simply returns a String indicating all the valid commands and their syntax.
	 * @return String: helpString indicating commands + syntaxes
	 */
	public String processHelp()
	{
		  return("Valid Commands are: (LOOK :== \"look\"  | DIG :== \"dig\" SPACE X SPACE Y  | FLAG  :== \"flag\" SPACE X SPACE Y | DEFLAG :== \"deflag\" SPACE X SPACE Y | HELP_REQ :== \"help\" | BYE :== \"bye\" ) NEWLINE. X and Y are ints.");				 
	}
	
	/**
	 * This is a very complicated method. It attempts to dig the current square. If the square's already
	 * been dug, it cancels and returns early with the current board state. Else, it carries out the dig. 
	 * If you dig an empty square, that square + all surrounding squares change to reflect the dig; 
	 * any squares with bombs next to them will reveal their counts.  If any of the surrounding squares
	 * also don't have bombs, the expansion continues from those squares to their adjacents, and so forth.
	 * 
	 * If you dig a bomb, some cleanup happens in which it is registered that you hit the bomb, the bomb is removed,
	 * the states are updated to reflect one less bomb, and a BOOM! message is returned, which should get
	 * you booted from the server by signaling to terminate your connection.
	 * @param input String in the format "dig X Y". 
	 * @return If no bomb, returns updated state of board.
	 * @return If bomb, returns BOOM!
	 */
	public synchronized String processDig(String input)
	{
		//boolean bombHit = false;
		//Input is dig_X_Y
		int locationDataX = Integer.valueOf(input.substring(4,5));
		int locationDataY = Integer.valueOf(input.substring(6,7));
		if(locationDataX < 0 || locationDataX >= this.size || locationDataY < 0 || locationDataY >= this.size)
		{
			return this.toString();
		}
		//At this point, we know the square indicated exists, so this next line is okay to do:
		Square requestedSquare = boardState.get(locationDataY).get(locationDataX);
		
		//If this is true, we've already dug it or flagged it so leave it be & return current state. 
		if(requestedSquare.getStatus() == "F" || (requestedSquare.getDescription() != "untouched" && requestedSquare.getDescription() != "bomb") )
		{
			return this.toString();
		}
		
		//Hard section. What to do if we get a bomb. 
		else if(requestedSquare.getDescription() == "bomb")
		{
			requestedSquare.setDescription("dug");
			//Clear the bomb away. We're going to cascade into the next section and explore this.
			//bombHit = true;
			//Compose an array list containing all squares adjacent to this one. 
			ArrayList<Square> adjacentToThisSquare = adjacentSquares(locationDataY,locationDataX);
			
			//We want to examine every square around these adjacent squares, and bombcount for all of them.
			for (Square s: adjacentToThisSquare)
			{
				int bombsFound = 0; //Number of adjacent bombs
				int xData = (int) s.getLocation().get(0); //This square's X location
				int yData = (int) s.getLocation().get(1); //This squares Y location
				ArrayList<Square> squaresAdjacentToSquaresAdjacentToThisSquare = adjacentSquares(xData,yData); //Use it to call adjacency again
				for (Square newLevel : squaresAdjacentToSquaresAdjacentToThisSquare) //For every square we just located:
				{
					if (newLevel.getDescription() == "bomb") //If that square is a bomb:
					{
						bombsFound +=1; //Increment our bombsFound counter.
					}
				}
				s.setCount(bombsFound);
				//Lastly, set the count of that square to bombsFound and restart, reinitializing everything for the next square.
			 
			}
			requestedSquare.setStatus(" ");
			ArrayList<Square> adjacents = adjacentSquares(locationDataX, locationDataY);
			Queue<Square> squareQueue = new LinkedBlockingQueue<Square>();
			squareQueue.addAll(adjacents);
			ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
			while (!squareQueue.isEmpty()){
				Square s = squareQueue.poll();
				visited.add(s.getLocation());
				if (s.getDescription() != "bomb" && s.getDescription() != "flagged")
				{
					s.setStatus(" ");
					int xLoc = (int) s.getLocation().get(0);
					int yLoc = (int) s.getLocation().get(1);
					ArrayList<Square> prospects = (adjacentSquares(yLoc,xLoc));
					for (Square m: prospects)
					{
						if(!visited.contains(m.getLocation()))
						{
							squareQueue.add(m);
							visited.add(m.getLocation());
						}
					}
					
				}
			}
			return "BOOM!";	
		}
		if (requestedSquare.getDescription() == "untouched")
			{
				requestedSquare.setStatus(" ");
				ArrayList<Square> adjacents = adjacentSquares(locationDataX, locationDataY);
				Queue<Square> squareQueue = new LinkedBlockingQueue<Square>();
				squareQueue.addAll(adjacents);
				ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
				while (!squareQueue.isEmpty())
				{
					Square s = squareQueue.poll();
					visited.add(s.getLocation());
					if (s.getDescription() != "bomb" && s.getDescription() != "flagged")
					{
						s.setStatus(" ");
						int xLoc = (int) s.getLocation().get(0);
						int yLoc = (int) s.getLocation().get(1);
						ArrayList<Square> prospects = (adjacentSquares(yLoc,xLoc));
						for (Square m: prospects)
						{
							if(!visited.contains(m.getLocation()))
							{
								squareQueue.add(m);
								visited.add(m.getLocation());
							}
						}
						
					}
				}
				return this.toString();	
			}
			
		
		
		else if (requestedSquare.getDescription() == "untouched"){
			requestedSquare.setStatus(" ");
			ArrayList<Square> adjacents = adjacentSquares(locationDataX, locationDataY);
			Queue<Square> squareQueue = new LinkedBlockingQueue<Square>();
			squareQueue.addAll(adjacents);
			ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
			while (!squareQueue.isEmpty()){
				Square s = squareQueue.poll();
				visited.add(s.getLocation());
				if (s.getDescription() != "bomb" && s.getDescription() != "flagged")
				{
					s.setStatus(" ");
					int xLoc = (int) s.getLocation().get(0);
					int yLoc = (int) s.getLocation().get(1);
					ArrayList<Square> prospects = (adjacentSquares(xLoc,yLoc));
					for (Square m: prospects)
					{
						if(!visited.contains(m.getLocation()))
						{
							squareQueue.add(m);
							visited.add(m.getLocation());
						}
					}
					
				}
			}
			
			return this.toString();
		}
		return this.toString();
	}
	
	/**
	 * This is a helper method created to acquire all the surrounding squares of any one given square.
	 * The central square is given by the two parameters of the method: The X indicates row from the
	 * top of the board down, and the Y indicates the column of the board from left to right.
	 * @param locationDataX
	 * @param locationDataY
	 * @return ArrayList<Square> containing all the adjacent squares to the given location
	 */
	public ArrayList<Square> adjacentSquares(int locationDataX, int locationDataY)
	{
		ArrayList<Square> adjacencyList = new ArrayList<Square>();
		int rightOne = locationDataX+1;
		int leftOne = locationDataX-1;
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
		
		
		
	}
	/**
	 * Lastly, this method is a helper method used in the constructor. We initialize a new Square
	 * for every slot in the board, and this method does that for whatever the given size is. 
	 * @return A row of brand-new squares to be added to the board. 
	 */
	public ArrayList<Square> createRow()
	{
		
		ArrayList<Square> row = new ArrayList<Square>();
		for(int j = 0; j < this.size; j++)
		{
			row.add(new Square(this.rowCounter, j));
		}
		this.rowCounter +=1;
		return row;
	}
}
