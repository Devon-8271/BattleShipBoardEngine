package edu.duke.ys507.battleship;

/**
 * Represents a BattleShip game Board with fixed dimentions. It is
 * generic in typename T, which is the type of information the view needs to
 * display this ship. 
 */

public interface Board<T> {
    /**
     * @return the board width, always positive.
     */
    public int getWidth();

    /**
     * @return the board height, always positive.
     */
    public int getHeight();


    /**
     * Takes a Coordinate and sees which Ship occupies that coordinates.
     * @param where is the Coordinate.
     * @return my displayInfo the BattleShipBoard has on those coordinates if any Ship is found.
     */
    public T whatIsAtForSelf(Coordinate where);

    /**
     * Takes a Coordinate and sees which Ship occupies that coordinates.
     * @param where is the Coordinate.
     * @return enemy's displayInfo the BattleShipBoard has on those coordinates if any Ship is found. 
     */
    public T whatIsAtForEnemy(Coordinate where);


    /**
     * Check the validity of the placement of the to be add Ship.
     * 
     * @return true if the placement is valid, and add the ship to the list. Else
     *         false.
     */
    public String tryAddShip(Ship<T> toAdd);


    /**
     * Fire at the coordinate c, check if any ship is hit and return the ship.
     * If no ship is hit, add c to enemyMisses and return null.
     * @param c is the coordinate fired at.
     * @return the ship if hit, otherwise null.
     */
    public Ship<T> fireAt(Coordinate c);

    /**
     * Check if all ships on the board are sunk.
     * @return true if all ships are sunk, otherwise false.
     */
    public Boolean checkForLose();

    /**
     * Check for win by seeing if enemy loses.
     * @param enemyBoard is enemy's board.
     * @return true if enemy loses, otherwise false.
     */
    public Boolean checkForWin(Board<T> enemyBoard);

    /**
     * @param where is the coordinate
     * @return the ship at the given coordinate
     */
    public Ship<T> getShipAt(Coordinate where);

    /**
     * @param s is the ship to remove.
     * @return true if s is found in the ship list and removed. Otherwise false.
     */
    public boolean removeShip(Ship<T> s);
}
