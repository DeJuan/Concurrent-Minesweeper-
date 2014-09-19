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
 * 
 *  @author DeJuan Anderson
 */
public class Board
{
	private final int size;
	private ArrayList<ArrayList<Square>> boardState;
	private int rowCounter = 0;
	
	public void checkRep(){
		assert(this.size >= 2);
		assert(boardState.get(0).size() == boardState.get(1).size());
		assert(boardState.get(0).get(0) != null);
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
		this.boardState = new ArrayList<ArrayList<Square>>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {
            String currentLine;
            while ((currentLine = br.readLine()) != null) 
            {
            	ArrayList<Square> row = new ArrayList<Square>();
            	String output = new String(currentLine);	
            	output = output.replaceAll("\\s", "");
            	for (int index = 0; index < output.length(); index++)
            	{
            		row.add(new Square(count, index, output.charAt(index)));
            	}
            	this.boardState.add(row);
                count +=1;
            }
        }
            catch(IOException e) 
            {
                e.printStackTrace();
            }

            this.size = count;
            checkRep();
            setAllCounts();
        
        
        
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
				boardString.append(this.boardState.get(row).get(col).toString());
				if (col != (this.size -1))
				{
					boardString.append(" ");
				}
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
	 * all the board information. This is direct rep exposure and should ONLY be used for debugging.
	 * It is left here in case future alterations need to be made to the class and then debugged.
	 * NEVER use this method in the Server class.
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
		while (!squareQueue.isEmpty())
		{
			Square currentSquare = squareQueue.poll();
			int bombsFound = 0;
			int x = (int) currentSquare.getLocation().get(0);
			int y = (int) currentSquare.getLocation().get(1);
			ArrayList<Square> currentAdj = adjacentSquares(x,y);
			for(Square s: currentAdj)
			{
				
				if (s.getDescription() == "bomb")
				{
					bombsFound += 1;
				}
				
				if (!visited.contains(s.getLocation()))
				{
					squareQueue.add(s);
					visited.add(s.getLocation()); //If we don't add it here, any adjacents to it will multi-count their adjacents, etc. 
				}
			}
			
			currentSquare.setCount(bombsFound);
		}
			
	}
	/**
	 * This method returns the current string representation of the board, as a player should see it.
	 * This was added just to keep things extremely clear. 
	 * @return String indicating the current Board state to a player.
	 */
	public synchronized String processLook()
	{
		return this.toString();
	}
	
	/**
	 * This is a secret method that I added for debugging mid-game and have decided to leave in
	 * for future fun. It allows one to cheat by getting the description of
	 * the square and its bomb count without digging it.
	 * 
	 *  Note, however, that this is the only method that players can issue which does not
	 *  actually have synchronization. I decided to intentionally leave it unsynchronized to add
	 *  potential danger to spying; are you actually sure that your information is accurate?
	 *  
	 * @param input
	 * @return Description of desired square.
	 */
	public String processSpy(String input)
	{
		int locationDataX;
		int locationDataY;
		if (input.length() == 7)
		{
		locationDataX = Integer.valueOf(input.substring(4,5));
		locationDataY = Integer.valueOf(input.substring(6,7));
		
		}
		
		else if (input.length() == 8)//spy_XX_Y or spy_X_YY
		{
			if ((0 <= Integer.valueOf(input.substring(5,6)) && Integer.valueOf(input.substring(5,6)) <= 9 ))
				{
				locationDataX = Integer.valueOf(input.substring(4,6));
				locationDataY = Integer.valueOf(input.substring(7,8));
				}
			else
				{
					locationDataX = Integer.valueOf(input.substring(4,5));
					locationDataY = Integer.valueOf(input.substring(6,8));
				}
			
		}
		else //spy_XX_YY
		{
			locationDataX = Integer.valueOf(input.substring(4,6));
			locationDataY = Integer.valueOf(input.substring(7,9));
		}
		
		if(locationDataX < 0 || locationDataX >= this.size || locationDataY < 0 || locationDataY >= this.size)
		{
			return this.toString();
		}
		return "Square description: " + boardState.get(locationDataY).get(locationDataX).getDescription() + "\n\rSquare bombcount: " + boardState.get(locationDataX).get(locationDataY).getCount();
	}
	
	/**
	 * This method allows a player to mark a square as flagged.
	 * No actions except for unflagging are available to a flagged square.
	 * @param input: A string in the format "flag X Y" 
	 * @return String representation of the currentBoard state with the updated Flag.
	 */
	public synchronized String processFlag(String input)
	{
		int locationDataX;
		int locationDataY;
		if (input.length() ==8)
		{
		locationDataX = Integer.valueOf(input.substring(5,6));
		locationDataY = Integer.valueOf(input.substring(7,8));
		
		}
		
		else if (input.length() == 8)//flag_XX_Y or flag_X_YY
		{
			if ((0 <= Integer.valueOf(input.substring(5,6)) && Integer.valueOf(input.substring(5,6)) <= 9 ))
				{
				locationDataX = Integer.valueOf(input.substring(5,7));
				locationDataY = Integer.valueOf(input.substring(8,9));
				}
			else
				{
					locationDataX = Integer.valueOf(input.substring(5,6));
					locationDataY = Integer.valueOf(input.substring(7,9));
				}
			
		}
		else //flag_XX_YY
		{
			locationDataX = Integer.valueOf(input.substring(5,7));
			locationDataY = Integer.valueOf(input.substring(8,10));
		}

		if(locationDataX < 0 || locationDataX >= this.size || locationDataY < 0 || locationDataY >= this.size)
		{
			return this.toString();
		}
		
		boardState.get(locationDataY).get(locationDataX).setStatus("F");
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
		int locationDataX;
		int locationDataY;
		if (input.length() == 10)
		{
		locationDataX = Integer.valueOf(input.substring(7,8));
		locationDataY = Integer.valueOf(input.substring(9,10));
		
		}
		
		else if (input.length() == 11)//deflag_XX_Y or deflag_X_YY
		{
			if ((0 <= Integer.valueOf(input.substring(7,8)) && Integer.valueOf(input.substring(8,9)) <= 9 ))
				{
				locationDataX = Integer.valueOf(input.substring(7,9));
				locationDataY = Integer.valueOf(input.substring(10,11));
				}
			else
				{
					locationDataX = Integer.valueOf(input.substring(7,8));
					locationDataY = Integer.valueOf(input.substring(9,11));
				}
			
		}
		else //deflag_XX_YY
		{
			locationDataX = Integer.valueOf(input.substring(7,9));
			locationDataY = Integer.valueOf(input.substring(10,12));
		}
		
		//Invalidity
		if(locationDataX < 0 || locationDataX >= this.size || locationDataY < 0 || locationDataY >= this.size)
		{
			return this.toString();
		}
		
		Square squareRequested = boardState.get(locationDataY).get(locationDataX);
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
		int locationDataX;
		int locationDataY;
		if (input.length() == 7)
		{
		locationDataX = Integer.valueOf(input.substring(4,5));
		locationDataY = Integer.valueOf(input.substring(6,7));
		
		}
		
		else if (input.length() == 8)//dig_XX_Y or dig_X_YY
		{
			if ((0 <= Integer.valueOf(input.substring(5,6)) && Integer.valueOf(input.substring(5,6)) <= 9 ))
				{
				locationDataX = Integer.valueOf(input.substring(4,6));
				locationDataY = Integer.valueOf(input.substring(7,8));
				}
			else
				{
					locationDataX = Integer.valueOf(input.substring(4,5));
					locationDataY = Integer.valueOf(input.substring(6,8));
				}
			
		}
		else //dig_XX_YY
		{
			locationDataX = Integer.valueOf(input.substring(4,6));
			locationDataY = Integer.valueOf(input.substring(7,9));
		}
		
		//Invalidity
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
			//Clear the bomb away. 
			requestedSquare.setStatus(" ");
			requestedSquare.setDescription("dug");

			//Compose an array list containing all squares adjacent to this one. 
			ArrayList<Square> adjacentToThisSquare = adjacentSquares(locationDataY,locationDataX);
			
			//We want to decrement the bomb count for each square adjacent to this one.
			for (Square s: adjacentToThisSquare)
			{	
				if(s.getCount() > 0)
				{
				s.setCount(s.getCount()-1);
				}
			}
			recursiveDig(requestedSquare, locationDataY, locationDataX);
			return "BOOM!";
		}
				
		//If we haven't dug it yet and its count is 0, we  launch the recursive discovery procedure. It has its own checks.
		if (requestedSquare.getDescription() == "untouched" && requestedSquare.getCount() == 0)
			{
				requestedSquare.setDescription("dug");
				requestedSquare.setStatus(" ");
				recursiveDig(requestedSquare, locationDataY, locationDataX);
			}
		
		//If we haven't dug it yet and it has a count, just reveal the count, update status.
		else if (requestedSquare.getDescription() == "untouched" && requestedSquare.getCount() != 0){
			requestedSquare.setStatus(requestedSquare.getCount());
			requestedSquare.setDescription("dug");
			return this.toString();
		}
		return this.toString();
	}
	
	public String recursiveDig(Square requestedSquare, int locationDataY, int locationDataX)
	{
			ArrayList<Square> adjacents = adjacentSquares(locationDataY, locationDataX);
			Queue<Square> squareQueue = new LinkedBlockingQueue<Square>();
			squareQueue.addAll(adjacents);
			ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
			while (!squareQueue.isEmpty())
			{
				Square s = squareQueue.poll();
				visited.add(s.getLocation());
				if (s.getDescription() == "bomb" || s.getStatus() == "F")
				{
					continue;
				}
				
				else
				{
					if (s.getCount() != 0)
					{
						s.setStatus(s.getCount());
						s.setDescription("dug");
						continue;
					}
				
					else if(s.getCount() == 0)
					{
						s.setStatus(" ");
						s.setDescription("dug");
						int xLoc = (int) s.getLocation().get(1);
						int yLoc = (int) s.getLocation().get(0);
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
	public ArrayList<Square> adjacentSquares(int locationDataY, int locationDataX)
	{
		ArrayList<Square> adjacencyList = new ArrayList<Square>();
		int rightOne = locationDataY+1;
		int leftOne = locationDataY-1;
		int upOne = locationDataX-1;
		int downOne = locationDataX+1;
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
			
			East = boardState.get(rightOne).get(locationDataX);
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
			North = boardState.get(locationDataY).get(upOne);
			adjacencyList.add(North);
		}
		
		if(downValid)
		{
			South = boardState.get(locationDataY).get(downOne);
			adjacencyList.add(South);
		}
		
		if(leftOne >= 0)
		{
			
			West = boardState.get(leftOne).get(locationDataX);
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
			row.add(new Square(j, this.rowCounter));
		}
		this.rowCounter +=1;
		return row;
	}
}
