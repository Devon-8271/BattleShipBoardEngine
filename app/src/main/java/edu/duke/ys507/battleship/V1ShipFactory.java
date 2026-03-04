package edu.duke.ys507.battleship;
/**
 * A version-1 ship factory that creates the standard set of Battleship ships
 * as RectangleShip objects.
 *
 * <p>
 * This factory interprets the Placement's orientation when constructing ships:
 * if the orientation is 'V', the ship is created with the given (width, height);
 * otherwise, the dimensions are swapped to represent a rotated ship.
 * </p>
 *
 * <p>
 * The ships created by this factory use their designated letter 
 * as the display character on the board, and use '*' as the hit character.
 * </p>
 */
public class V1ShipFactory implements AbstractShipFactory<Character> {
    /**
     * Create a ship with its placement, widthm height, letter and name with the orientation checked.
     * @param where is the placement of the ship.
     * @param w is the width (if vertically placed) of the ship.
     * @param h is the height (if vertically placed) of the ship.
     * @param letter is the displayed letter on the board of the ship.
     * @param name is the name of the ship.
     */
    protected Ship<Character> createRecShip(Placement where, int w, int h, char letter, String name) {
        char o = where.getOrientation();
        if (o != 'V' && o != 'H'){
            throw new IllegalArgumentException(name + "'s orientation must be H or V but is: " + o +"\n");
        }
        int width = (o == 'H') ? h:w;
        int height = (o == 'H') ? w:h;
        return new RectangleShip<Character>(name, where.getCoordinate(), width, height, letter, '*');
    }

    /**
     * Make a submarine.
     */
    @Override
    public Ship<Character> makeSubmarine(Placement where) {
        return createRecShip(where, 1, 2, 's', "Submarine");
    }

    /**
     * Make a Battleship.
     */
    @Override
    public Ship<Character> makeBattleship(Placement where) {
        return createRecShip(where, 1, 4, 'b', "Battleship");
    }

    /**
     * Make a Carrier.
     */
    @Override
    public Ship<Character> makeCarrier(Placement where) {
        return createRecShip(where, 1, 6, 'c', "Carrier");
    }

    /**
     * Make a Destroyer.
     */
    @Override
    public Ship<Character> makeDestroyer(Placement where) {
        return createRecShip(where, 1, 3, 'd', "Destroyer");
    }

}
