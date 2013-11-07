package ast;



import java.util.ArrayList;
import java.util.List;

public class DebugSquare 
{
	private String status;
	private String description;
	private int count;
	private final ArrayList<Integer> location = new ArrayList<Integer>();


	public DebugSquare(int x, int y)
	{
		location.add(x);
		location.add(y);
		if (x == 2 && y == 2){
			this.status = "-";
			this.description = "bomb";
		}
		
		else
		{
			this.status = "-";
			this.description = "untouched";

		}

	}

	public ArrayList<Integer> getLocation()
	{
		ArrayList<Integer> locationCopy = new ArrayList<Integer>();
		locationCopy.add(location.get(0));
		locationCopy.add(location.get(1));
		return locationCopy;
	}

	public String getStatus()
	{
		return this.status;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String s)
	{
		this.description = s;
	}

	public void setStatus(String s)
	{
		this.status = s;
		if (this.status == "-")
		{
			this.description = "untouched";
		}
		if(this.status == "F")
		{
			this.description = "flagged";
		}

		if (this.status == " ")
		{
			this.description = "dug";
			if(count != 0)
			{
				this.status = "" + count;
			}
		}
	}


	public void setCount(int i)
	{
		this.count = i;
		if (this.count == 0 && this.description != "untouched" && this.description != "bomb") 
		{
			this.status = " ";
		}

		else if(this.count != 0 && this.description != "untouched" && this.description != "bomb")
		{
			this.status = "" + this.count;
		}
	}

	public int getCount()
	{
		int countCopy = Integer.valueOf(this.count);
		return countCopy;
	}

	public String toString()
	{
		return this.status;
	}

}


