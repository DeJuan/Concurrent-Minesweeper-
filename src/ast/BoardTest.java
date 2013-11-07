package ast;

import org.junit.Test;
import static org.junit.Assert.*;


public class BoardTest {
	
	@Test
	public void checkPlainBoard()
	{
		Board testBoard = new Board();
		//Update: Need to iterate through this since I'm doing to string on the outer array,
		//And that outer array is a list of arrays, so it's sstill an object and calls
		//the default object toString which is why I'm getting the weird output!
		assertEquals(10,testBoard.getBoardSize());
		String[][] expectation = new String[10][10];
		for (int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				expectation[i][j] = "-";
			}
		}
		assertArrayEquals(expectation, testBoard.getBoardState());
		//System.out.println(testBoard.toString());
	}
	
	@Test
	public void checkBoardWithSizeSpec()
	{
		Board testBoard = new Board(15);
		assertEquals(15, testBoard.getBoardSize());
		String[][] expectation = new String[15][15];
		for (int i = 0; i < 15; i++)
		{
			for(int j = 0; j < 15; j++)
			{
				expectation[i][j] = "-";
			}
		}
		assertArrayEquals(expectation, testBoard.getBoardState());
	}

}
