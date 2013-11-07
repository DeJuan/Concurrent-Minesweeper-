package ast;

import java.util.ArrayList;

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
	
	@Test
	public void checkFlaggingASpace()
	{
		Board testBoard = new Board();
		assertEquals(10, testBoard.getBoardSize());
		String[][] expectation = new String[10][10];
		for (int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				expectation[i][j] = "-";
			}
		}
		expectation[5][5] = "F";
		testBoard.processFlag("flag 5 5");
		assertArrayEquals(expectation, testBoard.getBoardState());
		ArrayList<ArrayList<Square>> actualState = testBoard.getActualBoardStateForDebugPurposes();
		assertEquals("flagged", actualState.get(5).get(5).getDescription());
	}
	
	@Test
	public void checkFlaggingThenDeflaggingSpaces()
	{
		Board testBoard = new Board();
		String[][] expectation = new String[10][10];
		for (int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				expectation[i][j] = "-";
			}
		}
		expectation[5][5] = "F";
		expectation[2][3] = "F";
		testBoard.processFlag("flag 5 5");
		testBoard.processFlag("flag 2 3");
		assertArrayEquals(expectation, testBoard.getBoardState());
		testBoard.processDeflag("deflag 5 5");
		ArrayList<ArrayList<Square>> actualState = testBoard.getActualBoardStateForDebugPurposes();
		assertEquals("untouched", actualState.get(5).get(5).getDescription());
		expectation[5][5] = "-";
		assertArrayEquals(expectation, testBoard.getBoardState());
	}
	
	@Test
	public void testingHelpMessage(){
		Board testBoard = new Board();
		testBoard.processHelp();
	}

}
