package ast;

public class Square 
{
	private String status;
	private String description;
	private int count;
	
	public Square()
	{
		double decider = Math.random();
		if (decider < .25)
		{
			this.status = "-";
			this.description = "bomb";
		}
		else
		{
			setStatus("-");
		}
		
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public String getDescription()
	{
		return this.description;
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
		}
	}
	
	public void setStatus(int i)
	{
		this.count = i;
	}
	
	public void setCount(int i)
	{
		this.count = i;
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
