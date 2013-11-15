package ast;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents one square in the Minesweeper Board. It has various methods used to update the board state
 * and keep track of its own status. 
 * @author DeJuan Anderson
 *
 */
public class Square 
{
	private String status;
	private String description;
	private int count;
	private final ArrayList<Integer> location = new ArrayList<Integer>();
	
	/**
	 * Constructor for boards read in from a file, so the square knows whether it's a bomb or not.
	 */
	public Square(int x, int y, char determiner)
	{
		location.add(x);
		location.add(y);
		
		if (determiner == '1')
		{
			this.status = "-";
			this.description = "bomb";
		}
	
		else
		{
			this.status = "-";
			this.description = "untouched";
			
		}
	}
	
	/**
	 * Normal constructor for randomized squares. Takes in location data x and y for its location.
	 * @param x
	 * @param y
	 */
	public Square(int x, int y)
	{
		location.add(x);
		location.add(y);
		
		double decider = Math.random();
		if (decider < .25)
		{
			this.status = "-";
			this.description = "bomb";
		}
		
		else
		{
			this.status = "-";
			this.description = "untouched";
			
		}
		
	}
	/**
	 * Returns an array list representation of the location of the square.
	 * @return
	 */
	public ArrayList<Integer> getLocation()
	{
		ArrayList<Integer> locationCopy = new ArrayList<Integer>();
		locationCopy.add(location.get(0));
		locationCopy.add(location.get(1));
		return locationCopy;
	}
	
	/**
	 * Returns the string representation of what the string should appear to be.
	 * @return String representing status
	 */
	public String getStatus()
	{
		return this.status;
	}
	
	/**
	 * Returns a string representing the actual description of the square, such as "bomb".
	 * This is a seperate field from status, which could be "F" or "-" for a bomb. 
	 * @return
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Mutator method to change description when something happens, like digging.
	 * @param String indicating new description
	 */
	public void setDescription(String s)
	{
		this.description = s;
	}
	/**
	 * Mutator to change the status of a square, needed when we dig. 
	 * @param s
	 */
	public void setStatus(String s)
	{
		this.status = s;
		if (this.status == " ")
		{
			this.description = "dug";
			if(count != 0)
			{
				this.status = "" + count;
			}
		}
	}
	/**
	 * Overloaded mutator that lets us set status with the count, useful for stopping recursive dig or digging
	 * directly on a square with an adjacent bomb.
	 * @param i, the new status we'll be showing.
	 */
	public void setStatus(int i)
	{
		this.status = "" + i;
		this.description = "dug";
	}
	
	/**
	 * Mutator for setting the count of the square. Used when we blow up an adjacent bomb.
	 * @param i
	 */
	public void setCount(int i)
	{
		this.count = i;
		
	}
	/**
	 * Observer that just returns the current count of the square
	 * @return Integer for the count.
	 */
	public int getCount()
	{
		int countCopy = Integer.valueOf(this.count);
		return countCopy;
	}
	
	/**
	 * Returns string rep of the status.
	 */
	public String toString()
	{
		return this.status;
	}
	
}
