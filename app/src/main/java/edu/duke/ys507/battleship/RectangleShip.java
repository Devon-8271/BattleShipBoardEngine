package edu.duke.ys507.battleship;

import java.util.HashSet;

/**
 * A ship that occupies a rectangular area on the board.
 *
 * @param <T> the type used for display information
 */
public class RectangleShip<T> extends BasicShip<T> {
    /**
     * Computes the set of coordinates occupied by a rectangular ship.
     *
     * <p>
     * The rectangle starts at {@code upperLeft} and extends {@code width}
     * columns to the right and {@code height} rows downward.
     *
     * @param upperLeft the upper-left coordinate of the rectangle
     * @param width     the number of columns the rectangle occupies
     * @param height    the number of rows the rectangle occupies
     * @return a set containing all coordinates covered by the rectangle
     */
    static HashSet<Coordinate> makeCoords(Coordinate upperLeft, int width, int height) {
        HashSet<Coordinate> hash = new HashSet<>();
        for (int row = 0; row < height; ++row) {
            for (int column = 0; column < width; ++column) {
                hash.add(new Coordinate(upperLeft.getRow() + row, upperLeft.getColumn() + column));
            }
        }
        return hash;
    }

    private final String name;


    /**
     * Constructs a rectangular ship with the given display info.
     */
    public RectangleShip(String name, Coordinate upperLeft, int width, int height, ShipDisplayInfo<T> myinfo,
            ShipDisplayInfo<T> enemyinfo) {
        super(makeCoords(upperLeft, width, height), myinfo, enemyinfo);
        this.name = name;
    }

    /**
     * Constructs a rectangular ship with the given name, Coordinate info and hit
     * display values.
     * Tell the paraent constructor that
     * for my own view display
     * data if not hit
     * onHit if hit
     * 
     * for the enemy view,
     * nothing if not hit
     * data if hit
     */
    public RectangleShip(String name, Coordinate upperLeft, int width, int height, T data, T onHit) {
        this(name, upperLeft, width, height, new SimpleShipDisplayInfo<T>(data, onHit),
                new SimpleShipDisplayInfo<T>(null, data));
    }

    /**
     * Constructs a 1x1 ship with the given normal and hit display values.
     * The name is not passed in the constructor because this is only for testing in
     * previous tasks.
     */
    public RectangleShip(Coordinate upperLeft, T data, T onHit) {
        this("testship", upperLeft, 1, 1, data, onHit);
    }

    /**
     * return the name of this RectangleShip.
     */
    @Override
    public String getName() {
        return name;
    }
}
