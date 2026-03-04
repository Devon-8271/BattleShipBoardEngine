package edu.duke.ys507.battleship;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * A very simple computer-controlled player for Battleship Version 2.
 *
 * The computer places ships automatically (random valid placements) and
 * takes turns by firing in a deterministic scan order across the board.
 *
 * Behavior notes:
 * - No board state is displayed and no prompts are printed.
 * - Only the outcome of the action is printed (hit/miss message).
 * - This player does not use special actions (move/sonar). This is allowed.
 */
public class ComputerPlayer implements Player {
    private final Board<Character> theBoard;
    private final BoardTextView view;
    private final PrintStream out;
    private final String name;
    private final AbstractShipFactory<Character> factory;
    private final PlayerAction action = new PlayerAction();
    private HashMap<String, Function<Placement, Ship<Character>>> shipCreationFns;

    private final int height;
    private final int width;
    private final Random rng = makeRng();
    private int nextR = 0;
    private int nextC = 0;
    private int sonarRemaining = 3;
    private int moveRemaining = 3;

    // generate a seed to the Random.
    protected Random makeRng() {
        return new Random(2000);
    }

    /**
     * Constructs a ComputerPlayer.
     *
     * @param b       is the computer player's own board
     * @param out     is where the computer prints action outcomes
     * @param name    is the player name (typically "A" or "B")
     * @param factory creates Version 2 ships
     * @param height  is the board height
     * @param width   is the board width
     */
    public ComputerPlayer(Board<Character> b, PrintStream out, String name,
            AbstractShipFactory<Character> factory, int height, int width) {
        this.theBoard = b;
        this.view = new BoardTextView(b);
        this.out = out;
        this.name = name;
        this.factory = factory;
        this.height = height;
        this.width = width;
        this.shipCreationFns = new HashMap<>();
        setupShipCreationMap();
    }

    /**
     * Add ship creation function map to the shipCreationFns.
     */
    private void setupShipCreationMap() {
        shipCreationFns.put("Submarine", (p) -> factory.makeSubmarine(p));
        shipCreationFns.put("Destroyer", (p) -> factory.makeDestroyer(p));
        shipCreationFns.put("Battleship", (p) -> factory.makeBattleship(p));
        shipCreationFns.put("Carrier", (p) -> factory.makeCarrier(p));
    }

    /**
     * Picks a random coordinate that lies on the given board.
     *
     * @param b is the board to pick a coordinate on
     * @return a uniformly random valid Coordinate on b
     */
    private Coordinate randomCoordOn(Board<Character> b) {
        int r = rng.nextInt(b.getHeight());
        int c = rng.nextInt(b.getWidth());
        return new Coordinate(r, c);
    }

    /**
     * Attempts to move one of this player's ships to a random new placement.
     *
     * It chooses any existing ship coordinate on this player's board, then tries
     * a number of random placements until a move succeeds.
     *
     * @return true if a move succeeds, false otherwise
     */
    protected boolean tryRandomMove() {
        Coordinate oldC = findAnyShipCoordinate();
        if (oldC == null) {
            return false;
        }
        for (int tries = 0; tries < 200; tries++) {
            Placement np = randomPlacementForShipMove();
            String err = action.doMove(theBoard, shipCreationFns, oldC, np);
            if (err == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds any coordinate on this player's board that currently contains a ship.
     *
     * @return a Coordinate that has a ship, or null if none exist
     */
    private Coordinate findAnyShipCoordinate() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Coordinate cc = new Coordinate(r, c);
                if (theBoard.getShipAt(cc) != null) {
                    return cc;
                }
            }
        }
        return null;
    }

    /**
     * Generates a random placement string for attempting a move.
     *
     * The placement uses a random row/column and an orientation chosen according
     * to the ship type (Sub/Des use H/V; others use U/R/D/L).
     *
     * @return a random Placement to try for moving a ship
     */
    protected Placement randomPlacementForShipMove() {
        int r = rng.nextInt(height);
        int c = rng.nextInt(width);

        Ship<Character> any = theBoard.getShipAt(new Coordinate(r, c));
        String shipName = (any == null) ? "Submarine" : any.getName();

        char orient = randomOrientationFor(shipName);
        String s = "" + (char) ('A' + r) + c + orient;
        return new Placement(s);
    }

    /**
     * Generate random placement for the ship.
     * 
     * @param ship is the ship being placed.
     * @return the Placement
     */
    protected Placement randomPlacementFor(String ship) {
        int r = rng.nextInt(height);
        int c = rng.nextInt(width);
        char orient = randomOrientationFor(ship);
        String s = "" + (char) ('A' + r) + c + orient;
        return new Placement(s);
    }

    /**
     * randomly generate orientation for ships.
     * 
     * @param ship is the ship name.
     * @return a char the orientation.
     */
    protected char randomOrientationFor(String ship) {
        if (ship.equals("Submarine") || ship.equals("Destroyer")) {
            char result = rng.nextBoolean() ? 'H' : 'V';
            return result;
        } else {
            char[] orient = new char[] { 'U', 'R', 'D', 'L' };
            char result = orient[rng.nextInt(orient.length)];
            return result;
        }
    }

    /**
     * Creates one ship of the given type using the ship factory.
     */
    private Ship<Character> makeShip(String ship, Placement p) {
        if (ship.equals("Submarine")) {
            return factory.makeSubmarine(p);
        }
        if (ship.equals("Destroyer")) {
            return factory.makeDestroyer(p);
        }
        if (ship.equals("Battleship")) {
            return factory.makeBattleship(p);
        } else {
            return factory.makeCarrier(p);
        }
    }

    /**
     * place {@code count} numbers of ship to the board by repeatedly trying
     * random placements.
     */
    private void placeMany(String ship, int cnt) {
        for (int i = 0; i < cnt; ++i) {
            while (true) {
                Placement p = randomPlacementFor(ship);
                Ship<Character> s;
                s = makeShip(ship, p);
                String err = theBoard.tryAddShip(s);
                // keep try other ship if tryaddship fails.
                if (err == null) {
                    break;
                }
            }
        }
    }

    /**
     * Places all required ships for Version 2.
     *
     * The computer repeatedly tries random placements until each ship is placed.
     * No output is printed during placement.
     */
    @Override
    public void doPlacementPhaseV2() throws IOException {
        placeMany("Submarine", 2);
        placeMany("Destroyer", 3);
        placeMany("Battleship", 3);
        placeMany("Carrier", 2);
    }

    /**
     * Changes nextC and nextR with order.
     */
    private void nextCoor() {
        nextC++;
        if (nextC >= width) {
            nextC = 0;
            nextR++;
            if (nextR >= height) {
                nextR = 0;
            }
        }
    }

    /**
     * Returns the next coordinate to fire at.
     */
    protected Coordinate nextFireAt() {
        while (true) {
            Coordinate c = new Coordinate(nextR, nextC);
            nextCoor();
            return c;
        }
    }

    /**
     * Make a 20% special action rate.
     * 
     * @return true if 0 is picked from 0~5. Otherwise false.
     */
    protected boolean shoudUseSpecialAction() {
        return rng.nextInt(5) == 0;
    }

    protected boolean coinFlip() {
        return rng.nextBoolean();
    }

    /**
     * Plays one turn for the computer in Version 2.
     *
     * The computer always chooses to fire (no special actions) and fires at the
     * next coordinate in scan order. It prints only the outcome.
     *
     * @param enemyBoard is the opponent's board to fire at
     * @param enemyView  is unused by the computer player
     * @param enemyName  is unused by the computer player
     */
    @Override
    public void playOneTurnV2(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName)
            throws IOException {
        if (shoudUseSpecialAction()) {
            boolean canSonar = sonarRemaining > 0;
            boolean canMove = moveRemaining > 0 && findAnyShipCoordinate() != null;

            if (canSonar && canMove) {
                out.println("Player " + name + " used a special action");
                if (coinFlip()) {
                    action.doSonar(enemyBoard, randomCoordOn(enemyBoard));
                    sonarRemaining--;
                } else {
                    if (tryRandomMove()) {
                        moveRemaining--;
                    }
                }
                return;
            } else if (canSonar) {
                out.println("Player " + name + " used a special action");
                action.doSonar(enemyBoard, randomCoordOn(enemyBoard));
                sonarRemaining--;
                return;
            } else if (canMove) {
                out.println("Player " + name + " used a special action");
                if (tryRandomMove()) {
                    moveRemaining--;
                }
                return;
            }
        }

        Coordinate c = nextFireAt();
        Ship<Character> hit = enemyBoard.fireAt(c);
        if (hit != null) {
            out.println("Player " + name + " hit your " + hit.getName() + " at " + format(c));
        } else {
            out.println("Player " + name + " missed at " + format(c));
        }
    }

    /**
     * Prints the winning message for this computer player.
     */
    @Override
    public void announceWin() {
        out.print("PLAYER " + name + " WINS THE GAME!!!");
    }

    /**
     * Returns the computer player's own board.
     */
    @Override
    public Board<Character> getBoard() {
        return theBoard;
    }

    /**
     * Returns a view of the computer player's own board.
     */
    @Override
    public BoardTextView getView() {
        return view;
    }

    /**
     * Returns the computer player's name.
     */
    @Override
    public String getName() {
        return name;
    }

    private String format(Coordinate c) {
        return "" + (char) ('A' + c.getRow()) + c.getColumn();
    }

    /**
     * computer works the same for v1 and v2.
     * This is a place holder.
     */
    @Override
    public void doPlacementPhase() throws IOException {
        doPlacementPhaseV2();
    }

    /**
     * computer works the same for v1 and v2.
     * This is a place holder.
     */
    @Override
    public void playOneTurn(Board<Character> enemyBoard, BoardTextView enemyView, String enemyName) throws IOException {
        playOneTurnV2(enemyBoard, enemyView, enemyName);
    }
}
