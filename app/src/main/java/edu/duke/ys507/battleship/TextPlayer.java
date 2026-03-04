package edu.duke.ys507.battleship;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

/**
 * A text-based player for the Battleship game.
 *
 * <p>
 * A TextPlayer interacts with the user through a text interface. It reads
 * placement commands from an input stream, prints prompts and board states
 * to an output stream, and uses a ship factory to create ships.
 * </p>
 *
 * <p>
 * This class is designed for easy testing by allowing custom input and output
 * streams to be injected, rather than relying directly on System.in and
 * System.out.
 * </p>
 */

public class TextPlayer implements Player {
    final Board<Character> theBoard;
    final BoardTextView view;
    final BufferedReader inputReader;
    final PrintStream out;
    final AbstractShipFactory<Character> shipFactory;
    final String name;
    final ArrayList<String> shipsToPlace;
    final HashMap<String, Function<Placement, Ship<Character>>> shipCreationFns;
    final PlayerAction action = new PlayerAction();
    private int moveRemaining = 3;

    /**
     * @return moveRemaining
     */
    public int getMoveRemaining() {
        return moveRemaining;
    }

    private int sonarRemaining = 3;

    /**
     * @return sonarRemaining
     */
    public int getSonarRemaining() {
        return sonarRemaining;
    }

    /**
     * @return theBoard.
     */
    public Board<Character> getBoard() {
        return theBoard;
    }

    /**
     * @return view(BoardTextView)
     */
    public BoardTextView getView() {
        return view;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Constructs a {@code TextPlayer} that uses the given board, input source,
     * output
     * stream and name.
     *
     * <p>
     * Note: {@code inputSource} is wrapped by a {@link BufferedReader} to allow
     * convenient {@code readLine()} operations.
     *
     * @param theBoard        is the board to place ships on and display
     * @param inputSource     is where user input is read from (e.g.,
     *                        {@code new InputStreamReader(System.in)})
     * @param out             is where output is written to (e.g.,
     *                        {@code System.out})
     * @param name            is the name of the player(e.g., A).
     * @param factory         is a V1ShipFactory.
     * @param shipsToPlace    is an ArrayList of the ship names that we want to work
     *                        from.
     * @param shipCreationFns is a map from ship name to the lambda to create it.
     */
    public TextPlayer(Board<Character> theBoard, BufferedReader inputReader, PrintStream out, String name,
            AbstractShipFactory<Character> factory) {
        this.theBoard = theBoard;
        this.view = new BoardTextView(theBoard);
        this.inputReader = inputReader;
        this.out = out;
        this.shipFactory = factory;
        this.name = name;
        this.shipsToPlace = new ArrayList<String>();
        setupShipCreationList();
        this.shipCreationFns = new HashMap<String, Function<Placement, Ship<Character>>>();
        setupShipCreationMap();

    }

    /**
     * Prompts the user for a placement string, reads one line, and parses it into a
     * {@link Placement}.
     *
     * <p>
     * This method currently performs no validation beyond what {@link Placement}'s
     * constructor
     * does. If the input is not a valid placement, the exception will propagate to
     * the caller.
     *
     * @param prompt is printed (with a newline) before reading the user's input
     * @return a {@link Placement} parsed from the line of input
     * @throws IOException if an I/O error occurs while reading input
     */
    public Placement readPlacement(String prompt) throws IOException {
        out.println(prompt);
        String s = inputReader.readLine();
        if (s == null) {
            throw new EOFException();
        }
        return new Placement(s);
    }

    /**
     * add the ship creation functions we want to the ship creation function map.
     */
    protected void setupShipCreationMap() {
        shipCreationFns.put("Submarine", (p) -> shipFactory.makeSubmarine(p));
        shipCreationFns.put("Destroyer", (p) -> shipFactory.makeDestroyer(p));
        shipCreationFns.put("Battleship", (p) -> shipFactory.makeBattleship(p));
        shipCreationFns.put("Carrier", (p) -> shipFactory.makeCarrier(p));
    }

    /**
     * add the ships we want to work on to the ship creation list.
     */
    protected void setupShipCreationList() {
        shipsToPlace.addAll(Collections.nCopies(2, "Submarine"));
        shipsToPlace.addAll(Collections.nCopies(3, "Destroyer"));
        shipsToPlace.addAll(Collections.nCopies(3, "Battleship"));
        shipsToPlace.addAll(Collections.nCopies(2, "Carrier"));
    }

    /**
     * Reads one placement from input, create a Destroyer with that placement,
     * and prints the updated board to {@code out}.
     *
     * @param createFn is an object and apply method that takes a Placement and
     *                 returns a Ship<Character>
     * @throws IOException if reading input fails due to EOF or invalid Placement.
     */
    public void doOnePlacement(String shipName, Function<Placement, Ship<Character>> createFn) throws IOException {
        while (true) {
            Placement p;
            try {
                p = readPlacement("Player " + name + " where do you want to place a " + shipName + "?");
            } catch (EOFException e) {
                throw e;
            } catch (IllegalArgumentException e) {
                out.println("That placement is invalid: it does not have the correct format.");
                continue;
            }
            Ship<Character> s;
            try {
                s = createFn.apply(p);
            } catch (IllegalArgumentException e) {
                out.println("That placement is invalid: it does not have the correct format.");
                continue;
            }
            String err = theBoard.tryAddShip(s);
            if (err == null) {
                out.print(view.displayMyOwnBoard());
                return;
            }
            out.println(err);
        }

    }

    /**
     * Runs the ship placement phase for this player.
     *
     * <p>
     * This method prints the player's current board and a set of instructions, then
     * delegates to {@code doOnePlacement()} to interactively place the required
     * ships.
     * </p>
     *
     * @throws IOException if an I/O error occurs while reading user input during
     *                     placement
     */
    public void doPlacementPhase() throws IOException {
        out.print(view.displayMyOwnBoard());
        String message = "Player " + name + ": you are going to place the following ships (which are all\n" + //
                "rectangular). For each ship, type the coordinate of the upper left\n" + //
                "side of the ship, followed by either H (for horizontal) or V (for\n" + //
                "vertical).  For example M4H would place a ship horizontally starting\n" + //
                "at M4 and going to the right.  You have\n" + //
                "\n" + //
                "2 \"Submarines\" ships that are 1x2 \n" + //
                "3 \"Destroyers\" that are 1x3\n" + //
                "3 \"Battleships\" that are 1x4\n" + //
                "2 \"Carriers\" that are 1x6\n";
        out.print(message);
        for (int i = 0; i < shipsToPlace.size(); i++) {
            String shipName = shipsToPlace.get(i);
            doOnePlacement(shipName, shipCreationFns.get(shipName));
        }
    }

    /**
     * Runs the Version 2 ship placement phase for this player.
     *
     * Prints the player's current board and instructions for placing ships.
     * In Version 2, Submarines and Destroyers are placed with H/V orientations,
     * while Battleships and Carriers use U/R/D/L orientations. The coordinate
     * refers to the top-left corner of the smallest rectangle that contains the
     * ship.
     *
     * @throws IOException if reading input fails or reaches EOF
     */
    public void doPlacementPhaseV2() throws IOException {
        out.print(view.displayMyOwnBoard());
        String message = "Player " + name + ": you are going to place the following ships (which may not be\n" +
                "rectangular). For each ship, type the coordinate of the upper left corner of the\n" +
                "smallest rectangle that contains the ship, followed by an orientation letter.\n" +
                "\n" +
                "Submarines and Destroyers use H (horizontal) or V (vertical).\n" +
                "Battleships and Carriers use U, R, D, or L (up, right, down, left).\n" +
                "\n" +
                "You have\n" +
                "\n" +
                "2 \"Submarines\" ships that are 1x2 rectangles\n" +
                "3 \"Destroyers\" that are 1x3 rectangles\n" +
                "3 \"Battleships\" with 4 orientations (U/R/D/L)\n" +
                "2 \"Carriers\" with 4 orientations (U/R/D/L)\n";
        out.print(message);

        for (int i = 0; i < shipsToPlace.size(); i++) {
            String shipName = shipsToPlace.get(i);
            doOnePlacement(shipName, shipCreationFns.get(shipName));
        }
    }

    /**
     * Read String from inputreader and construct a coordinate.
     * 
     * @return the Coordinate
     * @throws IOException if the String does not follow the coordinate format.
     */
    private Coordinate readFireCoordinate() throws IOException {
        while (true) {
            out.println("Enter a coordinate to fire at: ");
            String s = inputReader.readLine();
            try {
                return new Coordinate(s);
            } catch (IllegalArgumentException e) {
                out.println("That coordinate is invalid");
            }
        }
    }

    /**
     * Prompts the user to enter a coordinate and keeps reading until a valid
     * coordinate is provided.
     *
     * @param prompt is printed before reading input.
     * @return the parsed Coordinate.
     * @throws IOException if reading input fails or reaches EOF.
     */
    private Coordinate readCoordinate(String prompt) throws IOException {
        while (true) {
            out.println(prompt);
            String s = inputReader.readLine();
            if (s == null) {
                throw new EOFException();
            }
            try {
                return new Coordinate(s.trim());
            } catch (IllegalArgumentException e) {
                out.println("That coordinate is invalid");
            }
        }
    }

    /**
     * Play one attacking turn. Prints both myboard and enemy's board side to side,
     * prompts the user
     * for a coordinate to fire at, and print whether it is a miss or hit.
     * 
     * @param enemyBoard is the enemy's board to fire at.
     * @param enemyView  is the view used to display enemy's board.
     * @param enemyName  is the enemy player's name.
     * @throws IOException if reading input fails.
     */
    public void playOneTurn(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName) throws IOException {
        out.print(name + "'s turn:\n");
        out.print(view.displayMyBoardWithEnemyNextToIt(enemyView, "Your ocean", "Player " + enemyName + "'s ocean"));
        Coordinate c = readFireCoordinate();
        Ship<Character> result = enemyBoard.fireAt(c);
        if (result != null) {
            out.println("You hit a " + result.getName() + "!");
        } else {
            out.println("You missed!");
        }
    }

    /**
     * prints the win message.
     */
    public void announceWin() {
        out.print("PLAYER " + name + " WINS THE GAME!!!");
    }

    /**
     * Do sonar scan by calling doSonar from action.
     * 
     * @param center is the center coordinate.
     * @return a string describing scan result.
     */
    public String doSonar(Coordinate center) {
        return action.doSonar(theBoard, center);
    }

    /**
     * move one ship on the board to another place on the board by calling doMove
     * from action.
     * 
     * @param oldCoordinate is the old coordinate in the ship.
     * @param newPlacement  is the final placement after the move.
     * @return a string containing error information.
     */
    public String doMove(Coordinate oldCoordinate, Placement newPlacement) {
        return action.doMove(theBoard, shipCreationFns, oldCoordinate, newPlacement);
    }

    /**
     * Reads the player's action choice for this turn.
     *
     * This method keeps prompting until the user enters a valid action letter.
     * Fire is always allowed. Move is only allowed if moveLeft is greater than 0.
     * Sonar is only allowed if sonarLeft is greater than 0.
     *
     * @param moveLeft  is the remaining number of move actions.
     * @param sonarLeft is the remaining number of sonar scans.
     * @return one of F, M, or S.
     * @throws IOException if reading input fails or reaches EOF.
     */
    private char readAction(int moveRemaining, int sonarRemaining) throws IOException {
        while (true) {
            String s = inputReader.readLine();
            if (s == null) {
                throw new EOFException();
            }
            s = s.toUpperCase();
            if (s.length() == 0) {
                continue;
            }
            char c = s.charAt(0);
            if (c == 'F') {
                return 'F';
            }
            if (c == 'M') {
                if (moveRemaining > 0) {
                    return 'M';
                }
                out.println("No moves remaining.");
                continue;
            }
            if (c == 'S') {
                if (sonarRemaining > 0) {
                    return 'S';
                }
                out.println("No sonar scans remaining.");
                continue;
            } else {
                out.println("Please enter F, M, or S.");
            }
        }
    }

    /**
     * Plays one full turn using the Version 2 rules.
     *
     * This method first displays the current boards (your board and the enemy board
     * side by side), then shows a menu of available actions:
     * fire, move (limited uses), and sonar scan (limited uses).
     *
     * The player is repeatedly prompted until a valid action is completed.
     * For move and sonar, the remaining-use counters are decreased only when the
     * action succeeds. Firing always ends the turn.
     *
     * @param enemyBoard is the opponent's board to act against.
     * @param enemyView  is the view used to display the opponent's board.
     * @param enemyName  is the opponent's player name.
     * @throws IOException if reading input fails or reaches EOF.
     */

    public void playOneTurnV2(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName)
            throws IOException {
        out.print(name + "'s turn:\n");
        out.print(view.displayMyBoardWithEnemyNextToIt(enemyView, "Your ocean", "Player " + enemyName + "'s ocean"));

        while (true) {
            out.println("Possible actions for Player " + name + ":\n");
            out.println(" F Fire at a square");
            if (moveRemaining > 0) {
                out.println(" M Move a ship to another square (" + moveRemaining + " remaining)");
            }
            if (sonarRemaining > 0) {
                out.println(" S Sonar scan (" + sonarRemaining + " remaining)");
            }
            out.println("\nPlayer " + name + ", what would you like to do?");

            char act = readAction(moveRemaining, sonarRemaining);

            if (act == 'F') {
                Coordinate c = readFireCoordinate();
                Ship<Character> result = enemyBoard.fireAt(c);
                if (result != null)
                    out.println("You hit a " + result.getName() + "!");
                else
                    out.println("You missed!");
                return;
            }

            if (act == 'M') {
                Coordinate oldCoordinate = readCoordinate("Player " + name + ", which ship do you want to move?");
                Placement newPlacement;
                try {
                    newPlacement = readPlacement("Player " + name + ", where do you want to move the ship?");
                } catch (IllegalArgumentException e) {
                    out.println("That placement is invalid: it does not have the correct format.");
                    continue;
                }
                String err = doMove(oldCoordinate, newPlacement);
                if (err != null) {
                    out.println(err);
                    continue;
                }
                moveRemaining--;
                return;
            }

            else {
                Coordinate center = readCoordinate("Enter the center coordinate for sonar scan: ");
                out.println(doSonar(center));
                sonarRemaining--;
                return;
            }
        }
    }

}
