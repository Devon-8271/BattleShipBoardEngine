package edu.duke.ys507.battleship;

import java.util.function.Function;

/**
 * This class handles textual display of
 * a Board (i.e., converting it to a string to show
 * to the user).
 * It supports two ways to display the Board:
 * one for the player's own board, and one for the
 * enemy's board.
 */

public class BoardTextView {
  /**
   * The Board to display
   */
  private final Board<Character> toDisplay;

  /**
   * Constructs a BoardView, given the board it will display.
   * 
   * @param toDisplay is the Board to display
   * @throws IllegalArgumentException if the board is larger than 10x26.
   */
  public BoardTextView(Board<Character> toDisplay) {
    this.toDisplay = toDisplay;
    if (toDisplay.getWidth() > 10 || toDisplay.getHeight() > 26) {
      throw new IllegalArgumentException(
          "Board must be no larger than 10x26, but is " + toDisplay.getWidth()
              + "x" + toDisplay.getHeight());
    }
  }

  /**
   * Display the view of player's own board by calling displayAnyBoard.
   * toDisplay.
   * This is temporary an empty board.
   */
  public String displayMyOwnBoard() {
    return displayAnyBoard((c) -> toDisplay.whatIsAtForSelf(c));
  }

  /**
   * Display the view of enemy's board by calling displayAnyBoard.
   * 
   * @return
   */
  public String displayEnemyBoard() {
    return displayAnyBoard((c) -> toDisplay.whatIsAtForEnemy(c));
  }

  /**
   * Displays the board by checking each coordinate using the given function.
   * 
   * @param getSquareFn a function that takes a Coordinate and returns a Character
   *                    to be shown at each square.
   * @return the board as a formatted String.
   */
  protected String displayAnyBoard(Function<Coordinate, Character> getSquareFn) {
    StringBuilder ans = new StringBuilder(makeHeader());
    for (char rowLetter = 'A'; rowLetter < 'A' + toDisplay.getHeight(); ++rowLetter) {
      ans.append(rowLetter).append(" ");
      String sep = "";
      for (int column = 0; column < toDisplay.getWidth(); ++column) {
        ans.append(sep);
        Coordinate c = new Coordinate(rowLetter - 'A', column);
        Character ch = getSquareFn.apply(c);
        if (ch == null) {
          ans.append(" ");
        } else {
          ans.append(ch);
        }
        sep = "|";
      }
      ans.append(" ").append(rowLetter).append("\n");
    }
    ans.append(makeHeader());
    return ans.toString();
  }

  /**
   * This makes the header line, e.g. 0|1|2|3|4\n
   * 
   * @return the String that is the header line for the given board
   */
  String makeHeader() {
    StringBuilder ans = new StringBuilder("  "); // README shows two spaces at the start
    String sep = ""; // start with nothing to separate, then switch to | to separate
    for (int i = 0; i < toDisplay.getWidth(); i++) {
      ans.append(sep);
      ans.append(i);
      sep = "|";
    }
    ans.append("\n");
    return ans.toString();
  }

  /**
   * Display my board with enemy's board(from my view) next to it including the headers.
   * @param enemyView is enemy's board text view.
   * @param myHeader
   * @param enemyHeader
   * @return a String to display both views.
   */
  public String displayMyBoardWithEnemyNextToIt(BoardTextView enemyView, 
                                                String myHeader, 
                                                String enemyHeader){
        String myBoard = this.displayMyOwnBoard();
        String enemyBoard = enemyView.displayEnemyBoard();
        String[] myStrings = myBoard.split("\n");
        String[] enemyStrings = enemyBoard.split("\n");
        StringBuilder sb = new StringBuilder();
        int w = this.toDisplay.getWidth();
        int h = this.toDisplay.getHeight();
        int myBodyWidth = 2 * w + 3;
        int enemyStart = 2 * w + 19;
        int myHeaderStart = 5;
        int enemyHeaderStart = 2 * w + 22; 
        sb.append(generateSpace(myHeaderStart)).append(myHeader).append(generateSpace(enemyHeaderStart - myHeaderStart - myHeader.length())).append(enemyHeader).append("\n");
        sb.append(myStrings[0]).append(generateSpace(enemyStart - myBodyWidth + 2)).append(enemyStrings[0]).append("\n");
        for (int i = 1; i < h; i++){
          sb.append(myStrings[i]);
          sb.append(generateSpace(enemyStart - myBodyWidth));
          sb.append(enemyStrings[i]).append("\n");
        } 
        return sb.toString();
    }
    
    /**
     * Generate given number of spaces.
     * @param n is the number of conssecutive spaces.
     * @return a String of n spaces.
     */
    private String generateSpace(int n){
      return " ".repeat(n);
    }
                                          

}
