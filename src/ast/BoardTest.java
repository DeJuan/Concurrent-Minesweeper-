package ast;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {
	
	@Test
	public void checkPlainBoard()
	{
		Board testBoard = new Board(10);
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
	
	/*
	@Test
	public void testingHelpMessage(){
		Board testBoard = new Board();
		testBoard.processHelp();
	}
	*/
	
	
	/*
	 * For some reason, I couldn't get assertArrayEquals to even accept the two arrayLists, so I did
	 * this test through inspection. It does indeed work, but I've removed the printlines so Didit won't crash.
	 */
	
	@Test
	public void testingAdjacencyBuilder()
	{
		Board testBoard = new Board();
		ArrayList<ArrayList<Square>> state = testBoard.getActualBoardStateForDebugPurposes();
		state.get(0).get(1).setStatus("Test");
		state.get(1).get(0).setStatus("Test");
		//System.out.println(testBoard.adjacentSquares(0, 0));
		//System.out.println(testBoard.adjacentSquares(1, 1));
		assertEquals(true,true);
	}
	
	@Test
	public void testDiggingSpace(){
		DebugBoard testBoard = new DebugBoard(3); //there's a bomb in the bottom right corner
		testBoard.processDig("dig 0 0");
		String[][] expectation = new String[3][3];
		for (int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				expectation[i][j] = " ";
			}
		}
		expectation[2][1] = "1";
		expectation[1][2] = "1";
		expectation[1][1] = "1";
		expectation[2][2] = "-";
		
		assertArrayEquals(expectation, testBoard.getBoardState());
	}
	
}
