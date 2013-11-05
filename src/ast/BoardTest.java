package ast;

import org.junit.Test;
import static org.junit.Assert.*;


public class BoardTest {
	
	@Test
	public void checkPlainBoard()
	{
		Board testBoard = new Board();
		System.out.println(testBoard.getBoardState().toString());
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
	}

}
