package edu.duke.ys507.battleship;

/**
 * A simple {@link ShipDisplayInfo} that returns one value normally and another when hit.
 *
 * @param <T> the type of display information
 */
public class SimpleShipDisplayInfo<T> implements ShipDisplayInfo<T> {
    T myData;
    T onHit;

    /**
     * Constructs a SimpleShipDisplayInfo
     * @param myData 
     * @param onHit
     */
    public SimpleShipDisplayInfo(T myData, T onHit) {
        this.myData = myData;
        this.onHit = onHit;
    }

    /**
     * check if (hit) and returns onHit if so, and myData otherwise.
     * 
     * @param where is the coordinate.
     * @param hit   is the boolean.
     * @return onHit if hit is true, else myData.
     */
    @Override
    public T getInfo(Coordinate where, boolean hit) {
        if (hit) {
            return onHit;
        } else {
            return myData;
        }
    }

}
