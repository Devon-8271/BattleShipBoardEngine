package edu.duke.ys507.battleship;

import java.util.HashSet;

public class CustomShip<T> extends BasicShip<T> {
    private final String name;

   

    /**
     * Make absolute coordinate according to the upperLeft coordinate, relative
     * coordinate, orientation, and size of the box.
     * 
     * @param upperLeft    is the top-left corner of the ship, i.e., the corner of
     *                     the smallest rectangle which fully encloses the ship.
     * @param relativeCoor is a HashSet of relative coordinates to the left right
     *                     corner of the ship.
     * @param boxW         is the width of the smallest rectangle which fully
     *                     encloses the ship
     * @param boxH         is the height of the smallest rectangle which fully
     *                     encloses the ship
     * @param orient       is the orientation of the custom ship. Only U, R, D, L
     *                     are accepted.
     * @return a HashSet of absolute coordinates of the custom ship.
     */
    static HashSet<Coordinate> makeCoords(Coordinate upperLeft, HashSet<Coordinate> relativeCoor,
            int boxW, int boxH, char orient) {
        HashSet<Coordinate> absCoor = new HashSet<>();
        for (Coordinate c : relativeCoor) {
            int row = c.getRow();
            int column = c.getColumn();
            int relativeR, relativeC;
            if (orient == 'U') {
                relativeR = row;
                relativeC = column;
            } else if (orient == 'R') {
                relativeR = column;
                relativeC = boxH - 1 - row;
            } else if (orient == 'D') {
                relativeR = boxH - 1 - row;
                relativeC = boxW - 1 - column;
            } else if (orient == 'L') {
                relativeR = boxW - 1 - column;
                relativeC = row;
            } else {
                throw new IllegalArgumentException("Invalid orientation: " + orient + "\n");
            }
            absCoor.add(new Coordinate(upperLeft.getRow() + relativeR, upperLeft.getColumn() + relativeC));
        }
        return absCoor;
    }

    /**
     * Constructs a Custom ship by calling the super constructor of basic ship.
     * 
     * @param name         is the name of the custom ship.
     * @param placement    is the placement of the custom ship.
     * @param relativeCoor is the relative coordinates of the ship to its upperLeft
     *                     corner.
     * @param boxW         is the width of the smallest rectangle which fully
     *                     encloses the ship
     * @param boxH         is the height of the smallest rectangle which fully
     *                     encloses the ship
     * @param data         is the letter display of the ship of hitted(eg. s, b).
     * @param onHit        is the onHit display of the ship(eg. *)
     */
    public CustomShip(String name, Placement placement, HashSet<Coordinate> relativeCoor, int boxW, int boxH, T data,
            T onHit) {
        super(makeCoords(placement.getCoordinate(), relativeCoor, boxW, boxH, placement.getOrientation()),
                new SimpleShipDisplayInfo<>(data, onHit), new SimpleShipDisplayInfo<>(null, data));
        this.name = name;
    }

    /**
     * return the name of the custom ship
     */
    @Override
    public String getName() {
        return name;
    }
}
