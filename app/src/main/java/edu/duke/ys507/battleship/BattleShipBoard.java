package edu.duke.ys507.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A concrete implementation of {@link Board} for the BattleShip game.
 * In this task, the board stores fixed dimensions (width and height),
 * enemyMisses(hashset of coordinates where enemy attack and miss).
 * Later tasks will extend this class to support ship placement, shots, and
 * queries.
 * It is generic in typename T, which is the type of information the view needs
 * to
 * display this ship.
 */
public class BattleShipBoard<T> implements Board<T> {

    HashSet<Coordinate> enemyMisses;
    private final int width;

    /**
     * @return the board width.
     */
    public int getWidth() {
        return width;
    }

    private final int height;

    /**
     * @return the board height.
     */
    public int getHeight() {

        return height;
    }

    private final ArrayList<Ship<T>> myShips;

    private final PlacementRuleChecker<T> placementChecker;

    private final T missInfo;

    private final HashMap<Coordinate, T> enemyHits;

    /**
     * Constructs a BattleShipBoard with the specified width and height
     * 
     * @param w    is the width of the newly constructed board.
     * @param h    is the height of the newly constructed board.
     *             myShips is an empty ArrayList.
     * @param miss is the missInfo of the board.
     * @throws IllegalArgumentException if the width or height are non-positive.
     */
    public BattleShipBoard(int w, int h, T miss) {

        if (w <= 0) {
            throw new IllegalArgumentException("BattleShipBoard's width must be positive but is " + w);
        }
        if (h <= 0) {
            throw new IllegalArgumentException("BattleShipBoard's height must be positive but is " + h);
        }
        this.width = w;
        this.height = h;
        this.myShips = new ArrayList<>();
        this.placementChecker = new InBoundsRuleChecker<T>(new NoCollisionRuleChecker<>(null));
        this.enemyMisses = new HashSet<>();
        this.missInfo = miss;
        this.enemyHits = new HashMap<>();
    }

    /**
     * Constructs a BattleShipBoard with the specified width, height and
     * PlacementRuleChecker.
     * 
     * @param w    is the width of the newly constructed board.
     * @param h    is the height of the newly constructed board.
     *             myShips is an empty ArrayList.
     * @param miss is the missInfo of the board.
     * @throws IllegalArgumentException if the width or height are non-positive.
     */
    public BattleShipBoard(int w, int h, PlacementRuleChecker<T> checker, T miss) {

        if (w <= 0) {
            throw new IllegalArgumentException("BattleShipBoard's width must be positive but is " + w);
        }
        if (h <= 0) {
            throw new IllegalArgumentException("BattleShipBoard's height must be positive but is " + h);
        }
        this.width = w;
        this.height = h;
        this.myShips = new ArrayList<>();
        this.placementChecker = checker;
        this.missInfo = miss;
        this.enemyHits = new HashMap<>();
    }

    /**
     * Check the validity of the placement of the to be add Ship.
     * 
     * @return true if the placement is valid, and add the ship to the list. Else
     *         false.
     */
    public String tryAddShip(Ship<T> toAdd) {
        String result = placementChecker.checkPlacement(toAdd, this);
        if (result != null) {
            return result;
        }
        myShips.add(toAdd);
        return null;
    }

    /**
     * return the ship information of my own ship
     */
    public T whatIsAtForSelf(Coordinate where) {
        return whatIsAt(where, true);
    }

    /**
     * return the information of enemy's ship
     * 
     * @param where
     * @return
     */
    public T whatIsAtForEnemy(Coordinate where) {
        return whatIsAt(where, false);
    }

    /**
     * Returns what should be displayed at the given coordinate.
     *
     * If viewing your own board, this shows the actual state:
     * it returns the ship’s display information if a ship is there,
     * otherwise null.
     *
     * If viewing the enemy’s board, this only shows information
     * that has been discovered:
     * - If the coordinate was previously hit, return the hit marker.
     * - If it was previously fired at and missed, return missInfo.
     * - Otherwise, return null, even if a ship is currently there.
     *
     * @param where  is the coordinate to check.
     * @param isSelf indicates whether this is the owner's view.
     * @return the display information at that coordinate, or null.
     */

    protected T whatIsAt(Coordinate where, boolean isSelf) {
        if (!isSelf) {
            if (enemyHits.containsKey(where)) {
                return enemyHits.get(where);
            }
            if (enemyMisses.contains(where)) {
                return missInfo;
            }
        }
        for (Ship<T> s : myShips) {
            if (s.occupiesCoordinates(where)) {
                return s.getDisplayInfoAt(where, isSelf);
            }
        }
        return null;
    }

    /**
     * Fires at the given coordinate.
     *
     * If a ship occupies that coordinate, record the hit and return the ship.
     * Otherwise, record a miss and return null.
     *
     * @param c is the coordinate to fire at.
     * @return the ship that was hit, or null if the shot missed.
     */

    public Ship<T> fireAt(Coordinate c) {
        for (Ship<T> s : myShips) {
            if (s.occupiesCoordinates(c)) {
                s.recordHitAt(c);
                enemyHits.put(c, s.getDisplayInfoAt(c, false));
                return s;
            }
        }
        enemyMisses.add(c);
        return null;
    }

    /**
     * Iterate through all the ships on the board to check if they are sunk.
     * 
     * @return true if all the ships are sunk, otherwise false.
     */
    public Boolean checkForLose() {
        for (Ship<T> s : myShips) {
            if (!s.isSunk()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check for win with enemyBoard.
     * 
     * @param enemyBoard is enemy's board.
     * @return true if enemy is lose, otherwise false.
     */
    public Boolean checkForWin(Board<T> enemyBoard) {
        if (enemyBoard.checkForLose()) {
            return true;
        }
        return false;
    }

    /**
     * @param where is the coordinate.
     * @return the ship at the coordinate on the board.
     */
    public Ship<T> getShipAt(Coordinate where) {
        for (Ship<T> s : myShips) {
            if (s.occupiesCoordinates(where)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @param s is the ship to remove.
     * @return true if s is found in the ship list and removed. Otherwise false.
     */
    public boolean removeShip(Ship<T> s) {
        return myShips.remove(s);
    }

}
