package edu.duke.ys507.battleship;

/**
 * Provides display information for a ship at a given coordinate.
 *
 * @param <T> the type of display information
 */

public interface ShipDisplayInfo<T> {
       public T getInfo(Coordinate where, boolean hit);

       
}
