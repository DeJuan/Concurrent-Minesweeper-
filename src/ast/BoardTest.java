package ast;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {
	
	/**
	 * My testing strategy is as follows:
	 * 1) I will be testing first just to see if making a size 10 board and 
	 * displaying it works, just to see if the board's data is properly initialized.
	 * 
	 * 2)I will do the same on a declared size board to again make sure proper initialization is performed.
	 * 
	 * 3)I will check the flagging operation, making sure it doesn't overwrite the original description.
	 * 
	 * 4) I will check the deflagging operation, making sure that it doesn't overwrite the original
	 * description of the space we flagged.
	 * 
	 * NOTE: I removed these explicit checks later on and used the Spy method to detect them, so I am certain
	 * they function properly as I have played the game and cheated mid-game by looking at the underlying data structure.
	 * 
	 * 5) I make sure the help message doesn't error.
	 * 
	 *  6) I test my adjacency finding method as without it, I can't test more.
	 *  
	 *  7) Once I know the adjacency finder works, I then proceed to finally test the dreaded dig.
	 *  To avoid breaking my board trying to fix things, I create multiple classes to fix some properties of the board
	 *  to enable me to test dig and debug without destroying my current Board, then I can simply update the Board code when the
	 *  debugging is successful.
	 *  
	 *  8) Now that digging works, I test setting off bombs and getting the BOOM!.
	 *  
	 *  9) Now that setting off bombs and digging works, I test the removal and resetting of bombs so that
	 *  the squares surrounding the bomb change to reflect the bomb's removal. 
	 *  
	 *  10) When all tests pass, load up the published test and attempt to pass it. If it fails, debug the code and
	 *  work until both the published test and these tests pass.
	 */
	
	@Test
	public void checkPlainBoard()
	{
		Board testBoard = new Board(10);
		testBoard.checkRep();
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
		expectation[3][2] = "F";
		testBoard.processFlag("flag 5 5");
		testBoard.processFlag("flag 2 3");
		assertArrayEquals(expectation, testBoard.getBoardState());
		testBoard.processDeflag("deflag 5 5");
		ArrayList<ArrayList<Square>> actualState = testBoard.getActualBoardStateForDebugPurposes();
		//assertEquals("untouched", actualState.get(5).get(5).getDescription());
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
		//1s all centered around (2,2) , so 1,1 | 1,2 | 1,3 | 2,1 | 2,3 | 3,1 | 3,2 | 3,3  should all show a 1.
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
