// Author: Chris Winsor
// ID: V00767709 Title: Mine Sweeper
// Program Description: Simulates the game Mine Sweeper by getting input from user and printing the board to the command window.

import java.io.*;
import java.util.*;

public class MineSweeper {

	public static void main( String [] args) {
		Scanner input = new Scanner(System.in);

		//creates the board and places values in it
		int [][] board = new int [10][10];//makes it one bigger to prevent crashing during the exposeSquare method
		boolean [][] board2 = new boolean [10][10];//makes it one bigger to prevent crashing during the exposeSquare method
		initializeBoard(board);

		int outcome = 0;//holds the outcome of the turns, 0 is neutral and 1 is a win or loss

		//keeps the turns going until the user wins or loses
		while (outcome != 1) {
			drawBoard(board2, board);
			outcome = checkCoordanite( board, board2, input);
		}
	}
	//places the bombs and the values into the board
	public static void initializeBoard (int [][] board) {
		placeBombs(board);

		//fills the board and not the border
		for(int i=1; i < board.length-1; i++){
			for(int j=1; j < board [i].length-1; j++){
				if(board [i] [j] != -1) {//places in a value if the spot is not a bomb
					board [i] [j] = fillSpot( i, j, board);
				}
			}
		}
	}
	//prints the board revealing only the spots chosen by the user
	public static void drawBoard( boolean [][] boardB, int [][] boardI) {
		System.out.println("  | 1 2 3 4 5 6 7 8");
		System.out.println("___________________");
		for(int i=1; i < boardB.length - 1; i++){
			System.out.print(i + " | ");
			for(int j=1; j < boardB [i].length - 1; j++){
				if(boardB [i][j] == false) {
					System.out.print(". ");
				}else {
					System.out.print(boardI [i][j] + " ");
				}
			}
			System.out.println();
		}
	}
	//gets a coordinate from the user and checks if to expose it or if it is a win or loss
	public static int checkCoordanite( int [][] boardI, boolean [][] boardB, Scanner input) {
		int row = 1;
		int column = 1;
		int holder = 0;/*when 0 it starts the loop. when -1 the coordinate entered was out of range and asks for a new one.
					     when 1 a correct coordinate was entered*/

		while (holder < 1) {
			//prints if re-entered
			if (holder == -1) {
				System.out.println("The number entered is out of range");
			}
			System.out.println("Enter a column number (a digit between 1 and 8)");
			column = input.nextInt();
			if (column < 1 || column > 8) {
				holder = -1;
			}else {
				holder = 1;
			}
		}
		holder = 0;//resets to neutral

		while (holder < 1) {
			//prints if re-entered
			if (holder == -1) {
				System.out.println("The number entered is out of range");
			}
			System.out.println("Enter a row number (a digit between 1 and 8)");
			row = input.nextInt();
			if (column < 1 || column > 8) {
				holder = -1;
			}else {
				holder = 1;
			}
		}

		//initiates a loss if the coordinate is a bomb
		if ( boardI [row][column] == -1) {
			lose(boardB, boardI, row, column);
			return 1;
		}
		boardB [row][column]= exposeSquare( row, column, boardB, boardI);//exposes the square if it is not a bomb

		//initiates a win if all the coordinates are revealed
		if ( checkWin(boardB) == 54) {
			win(boardB, boardI);
			return 1;
		}
		return 0;
	}
	//places 10 randomly placed bombs
	public static void placeBombs( int [][] board) {
		Random r = new Random();
		int counter = 0;
		while ( counter < 10) {
			int column = r.nextInt(8)+1;
			int row = r.nextInt(8)+1;
			if ( board [column][row] > -1) {
				board [column][row] = -1;
				counter++;
			}
		}
	}
	//fills the spot with the number of bombs adjacent to the spot
	public static int fillSpot( int col, int row, int [][] board) {
		int counter = 0;//keeps track of the number of bombs
		for (int i = col + 1; i >= col - 1; i--) {
			for (int j = row - 1; j <= row + 1; j++) {
				if (board [i][j] == -1) {
					counter++;//adds one to the counter if it was a bomb
				}
			}
		}
		return counter;
	}
	//exposes the spot to the user for the duration of the game
	public static boolean exposeSquare( int row, int col, boolean [][] boardB, int [][] boardI) {
		//exposes all the adjacent spots if the coordinates value was 0
		if ( boardI [row][col] == 0) {
			for (int i = row-1; i <= row+1; i++) {
				for (int j = col-1; j <= col+1; j++) {
					boardB [i][j] = true;
				}
			}
		}
		return true;
	}
	//checks the number of spots exposed
	public static int checkWin(boolean [][] boardB) {
		int counter = 0;
		for(int i=1; i < boardB.length - 1; i++){
			for(int j=1; j < boardB [i].length - 1; j++){
				if (boardB [i][j] == true) {
					counter++;
				}
			}
		}
		return counter;
	}
	//prints the full board when they win
	public static void win( boolean [][] boardB, int [][] boardI) {
		System.out.println("  | 1 2 3 4 5 6 7 8");
		System.out.println("___________________");
		for(int i=1; i < boardB.length - 1; i++){
			System.out.print(i + " | ");
			for(int j=1; j < boardB [i].length - 1; j++){
				if (boardI [i][j] == -1) {
					System.out.print("* ");
				}else {
					System.out.print(boardI [i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println("Congratulations! You Won!");
	}
	//shows all the bombs and the bomb that they hit to lose
	public static void lose( boolean [][] boardB, int [][] boardI, int row, int col) {
		System.out.println("  | 1 2 3 4 5 6 7 8");
		System.out.println("___________________");
		for(int i=1; i < boardB.length - 1; i++){
			System.out.print(i + " | ");
			for(int j=1; j < boardB [i].length - 1; j++){
				if (boardI [i][j] == -1) {
					if ((i == row) && (j == col)) {
						System.out.print("X ");
					}else {
						System.out.print("* ");
					}
				}else if (boardB [i][j] == false) {
					System.out.print(". ");
				}else {
					System.out.print(boardI [i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println("Kaboom! Game Over!");
	}
}