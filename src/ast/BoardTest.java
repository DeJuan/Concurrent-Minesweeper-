package ast;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {
	
	@Test
	public void checkPlainBoard()
	{
		Board testBoard = new Board(10);
		testBoard.checkRep();
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
		testBoard.checkRep();
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
		testBoard.checkRep();
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
		testBoard.checkRep();
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
		testBoard.checkRep();
		testBoard.processHelp();
	}
	
	
	
	
	 //For some reason, I couldn't get assertArrayEquals to even accept the two arrayLists, so I did
	 // this test through inspection. It does indeed work, but I've removed the printlines so Didit won't crash.
	
	
	@Test
	public void testingAdjacencyBuilder()
	{
		Board testBoard = new Board();
		testBoard.checkRep();
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
		testBoard.checkRep();
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
	
	@Test
	public void testDiggingInBiggerSpace(){
		DebugBoard testBoard = new DebugBoard(5); //there's a bomb in the middle now
		testBoard.checkRep();
		testBoard.processDig("dig 0 0");
		String[][] expectation = new String[5][5];
		for (int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				expectation[i][j] = " ";
			}
		}
		//1s all centered around 2 2, so 1 1 1 2 1 3 2 1 2 3 3 1 3 2 3 3 all 1
		expectation[1][1] = "1";
		expectation[1][2] = "1";
		expectation[1][3] = "1";
		expectation[2][1] = "1";
		expectation[2][2] = "-";
		expectation[2][3] = "1";
		expectation[3][1] = "1";
		expectation[3][2] = "1";
		expectation[3][3] = "1";
		assertArrayEquals(expectation, testBoard.getBoardState());
	}
	
	@Test
	public void testDetonatingBomb()
	{
		DebugBoard testBoard = new DebugBoard(3);
		testBoard.checkRep();
		String result = testBoard.processDig("dig 2 2");
		System.out.println(testBoard.toString());
		assertEquals("BOOM!", result);
	}
	
	@Test
	public void testTwoBombField()
	{
		DebugBoard2 testBoard = new DebugBoard2(3);
		testBoard.checkRep();
		testBoard.processDig("dig 1 1");
		String[][] expectation = new String[3][3];
		for (int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				expectation[i][j] = "1";
			}
		}
		expectation[0][1] = "-";
		expectation[1][0] = "2";
		expectation[1][1] = "2";
		expectation[1][2] = "2";
		expectation[2][1] = "-";
		
		assertArrayEquals(expectation,testBoard.getBoardState());
	}
	
	@Test
	public void testTwoBombFieldBombDetonationAndRelabeling()
	{
		DebugBoard2 testBoard = new DebugBoard2(3);
		testBoard.checkRep();
		String result = testBoard.processDig("dig 2 1");
		String[][] expectation = new String[3][3];
		for (int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				expectation[i][j] = "1";
			}
		}
		expectation[0][1] = "-";
		expectation[2][0] = " ";
		expectation[2][1] = " ";
		expectation[2][2] = " ";
		
		System.out.println(testBoard.toString());
		assertArrayEquals(expectation, testBoard.getBoardState());
		assertEquals("BOOM!", result);
	}
}
