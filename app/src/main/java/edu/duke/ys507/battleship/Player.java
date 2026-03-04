package edu.duke.ys507.battleship;

import java.io.IOException;

/**
 * Represents a Battleship game participant (human or computer).
 *
 * <p>
 * A {@code Player} owns a board (their ships), can place ships during a
 * placement phase,
 * and can take turns attacking an enemy board. This interface supports both the
 * original
 * turn/placement flow and an extended V2 flow (e.g., additional ship
 * orientations or special actions),
 * exposed via the {@code *V2} methods.
 */
public interface Player {

  /**
   * Runs the original ship placement phase for this player.
   *
   * @throws IOException if reading input fails or an output stream error occurs
   */
  void doPlacementPhase() throws IOException;

  /**
   * Plays one turn in the original rule set against the given enemy.
   *
   * @param enemyBoard is the enemy's board to attack
   * @param enemyView  is a view for displaying the enemy board
   * @param enemyName  is the enemy player's name (for messages)
   * @throws IOException if reading input fails or an output stream error occurs
   */
  void playOneTurn(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName) throws IOException;

  /**
   * Plays one turn in the V2 rule set against the given enemy.
   *
   * @param enemyBoard is the enemy's board to attack
   * @param enemyView  is a view for displaying the enemy board
   * @param enemyName  is the enemy player's name (for messages)
   * @throws IOException if reading input fails or an output stream error occurs
   */
  void playOneTurnV2(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName) throws IOException;

  /**
   * Runs the V2 ship placement phase for this player.
   *
   * @throws IOException if reading input fails or an output stream error occurs
   */
  public void doPlacementPhaseV2() throws IOException;

  /**
   * Announces that this player has won the game.
   */
  void announceWin();

  /**
   * Returns this player's board (their own ships).
   *
   * @return the board owned by this player
   */
  Board<Character> getBoard();

  /**
   * Returns a text view for displaying this player's board.
   *
   * @return the view associated with this player
   */
  BoardTextView getView();

  /**
   * Returns this player's name.
   *
   * @return the player's name
   */
  String getName();

}
