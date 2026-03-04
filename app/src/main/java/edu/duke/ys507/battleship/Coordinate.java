package edu.duke.ys507.battleship;
/**
 * Represent a coordinate on a Battleship board.
 * <p>
 * A coordinate is identified by a row and a column.
 * 
 */
public class Coordinate {
    private final int row;

    /**
     * @return the Coordinate row.
     */
    public int getRow() {
        return row;
    }

    private final int column;

    /**
     * @return the Coordinate column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Constructs a Coordinate.
     * 
     * @param r is the row of the Coordinate.
     * @param c is the column of the Coordinate.
     */
    public Coordinate(int r, int c) {
        this.row = r;
        this.column = c;
    }

    /**
     * Compare this Coordinate with another object for equality.
     * @param o the Object to compare with.
     * @return true if the object is the same class of this Coordinate, and has the same
     * row and column. Else false.
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            Coordinate c = (Coordinate) o;
            return row == c.row && column == c.column;
        }
        return false;
    }

    /**
     * @return a String representation of this Coordinate.
     */
    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
    /**
     * @return a hashcode for this Coordinate.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Constructs a Coordinate from a two-character description like "A0".
     * @param descr is the input String.
     * @throws IllegalArgumentException if descr' length is not 2.
     * @throws IllegalArgumentException if the first char of descr is out of A-Z.
     * @throws IllegalArgumentException if the second char of descr is not an integer from 0 to 9.
     * The first char of descr is converted to int to be this Coordinate's row.
     * The second char of descr is converted to int to be this Coordinate's column.
     */
    public Coordinate(String descr){
        if (descr.length() != 2){
            throw new IllegalArgumentException("Expected descr length of 2, but has " + descr.length() + "\n");
        }
        char rowLetter = Character.toUpperCase(descr.charAt(0));
        int columnLetter = descr.charAt(1) - '0';
        if (rowLetter < 'A' || rowLetter > 'Z'){
            throw new IllegalArgumentException("rowLetter " + rowLetter + " is out of range\n");
        }
        if (columnLetter < 0 || columnLetter > 9){
            throw new IllegalArgumentException("columnLetter " + columnLetter + " is out of range\n");
        }

        this.row = rowLetter - 'A';
        this.column = columnLetter;
    }
}
