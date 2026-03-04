package edu.duke.ys507.battleship;

/**
 * Describes the placement on the board.
 * A placement is described by a coordinate, and an orientation ('H' or 'V' for Version1).
 */
public class Placement {
    private final Coordinate coordinate;

    /**
     * 
     * @return the coordinate.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    private final char orientation;

    /**
     * 
     * @return the orientation.
     */
    public char getOrientation() {
        return orientation;
    }

    /**
     * Constructs a Placement.
     * 
     * @param c is the coordinate.
     * @param o is the orientation, is stored in UpperCase.
     */
    public Placement(Coordinate c, char o) {
        this.coordinate = c;
        this.orientation = Character.toUpperCase(o);
    }

    /**
     * @return a String representation of this Placement in the form like (c1, h).
     */
    @Override
    public String toString() {
        return "(" + coordinate + ", " + orientation + ")";
    }

    /**
     * @return a hashcode for this Placement.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Compare this Placement with another object for equality.
     * 
     * @param o the Object to compare with.
     * @return true if the object is the same class of this Placement, and has the
     *         same
     *         coordinate and orientation. Else false.
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            Placement p = (Placement) o;
            return coordinate.equals(p.coordinate) && orientation == p.orientation;
        }
        return false;
    }

    /**
     * Constructs a Placement given a string of form such as {@code "A0H"}.
     * 
     * @param s is the input string.
     *          The first two characters specify the upper-left coordinate (e.g.,
     *          {@code "A0"})
     *          The third character specifies the orientation. For Version 1, only
     *          {@code 'H'} (horizontal)
     *          and {@code 'V'} (vertical) are allowed; lowercase letters are also
     *          accepted.
     *          Updated in version2 to allow {@code 'HVRUDL'}
     * @throws IllegalArgumentException if length of s is not 3, or orientation is
     *                                  not in "HVRUDL"(lowerCase is also
     *                                  acceptable).
     * 
     */
    public Placement(String s) {
        if (s.length() != 3) {
            throw new IllegalArgumentException("Expected String length 3, but has: " + s.length());
        }
        Coordinate c = new Coordinate(s.substring(0, 2));
        char o = Character.toUpperCase(s.charAt(2));
        if ("HVRUDL".indexOf(o)==-1) {
            throw new IllegalArgumentException("Orientation must be one of H,V,U,R,D,L, but is: " + s.charAt(2));
        }
        this.coordinate = c;
        this.orientation = o;

    }

}
