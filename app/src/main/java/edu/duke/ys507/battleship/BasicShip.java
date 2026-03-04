package edu.duke.ys507.battleship;

import java.util.HashMap;

/**
 * An abstract base class for ships in the Battleship game.
 *
 * <p>
 * A {@code BasicShip} tracks which coordinates it occupies and whether each
 * occupied coordinate has been hit. It also provides display information for
 * both the owning player and the enemy player.
 * </p>
 *
 * @param <T> the type used for display information
 */
public abstract class BasicShip<T> implements Ship<T> {

    protected ShipDisplayInfo<T> myDisplayInfo;
    protected HashMap<Coordinate, Boolean> myPieces;
    protected ShipDisplayInfo<T> enemyDisplayInfo;

    /**
     * Constructs a Basicship given a Iterable of coordinates.
     * 
     * @param where            is the iterable of Coordinates.
     * @param myDisplayInfo    is the display information of this Ship.
     * @param enemyDisplayInfo is the display information of enemy's Ship.
     *                         add each coordinate to myPieces, they are not hit so
     *                         the
     *                         boolean is false.
     */
    public BasicShip(Iterable<Coordinate> where, ShipDisplayInfo<T> myDisplayInfo,
            ShipDisplayInfo<T> enemyDisplayInfo) {
        myPieces = new HashMap<Coordinate, Boolean>();
        for (Coordinate c : where) {
            myPieces.put(c, false);
        }
        this.myDisplayInfo = myDisplayInfo;
        this.enemyDisplayInfo = enemyDisplayInfo;
    }

    /**
     * Obtain the displayInfo at a coordinate in this Ship.
     * If myShip is true, use myDisplayInfo to call getInfo on, otherwise,
     * use enemmyDisplayInfo to call getInfo on.
     * 
     * @return generic type T of the info.
     */
    @Override
    public T getDisplayInfoAt(Coordinate where, boolean myShip) {
        if (myShip) {
            return myDisplayInfo.getInfo(where, wasHitAt(where));
        }
        return enemyDisplayInfo.getInfo(where, wasHitAt(where));
    }

    /**
     * Check if every coordinate in this Ship is hit.
     * 
     * @return true if all hit, else false.
     */
    @Override
    public boolean isSunk() {
        for (Boolean hit : myPieces.values()) {
            if (!hit) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param where is the coordinate.
     * @return true if this ship occupied the coordinate, false else.
     */
    @Override
    public boolean occupiesCoordinates(Coordinate where) {
        return myPieces.containsKey(where);
    }

    /**
     * Record the hit status of an coordinate.
     * 
     * @param where is the hit coordinate.
     */
    @Override
    public void recordHitAt(Coordinate where) {
        checkCoordinateInThisShip(where);
        myPieces.put(where, true);
    }

    /**
     * check if a coordinate in the Ship was hit.
     * 
     * @param where is the coordinate.
     * @return true if is hit, false else.
     */
    @Override
    public boolean wasHitAt(Coordinate where) {
        checkCoordinateInThisShip(where);
        return myPieces.get(where);
    }

    /**
     * Check if the coordinate is in the Ship
     * 
     * @param c is the coordinate.
     * @throws IllegalArgumentException if c is not in this Ship.
     */
    protected void checkCoordinateInThisShip(Coordinate c) {
        if (!myPieces.containsKey(c)) {
            throw new IllegalArgumentException("The coordinate (" + c.getRow() + "," +
                    c.getColumn() + ") is not in this Ship\n");
        }
    }

    /**
     * Get all of the Coordinates that this Ship occupies.
     * 
     * @return An Iterable with the coordinates that this Ship occupies
     */
    @Override
    public Iterable<Coordinate> getCoordinates() {
        return myPieces.keySet();
    }

}
