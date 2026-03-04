package edu.duke.ys507.battleship;

import java.util.HashSet;

public class V2ShipFactory extends V1ShipFactory {
    /**
     * Orientation checking.
     */
    private static void checkOrientation(char o, String allow, String shipName) {
        if (allow.indexOf(o) == -1) {
            throw new IllegalArgumentException(
                    shipName + "'s orientation must be one of " + allow + " but is: " + o + "\n");
        }
    }

    /**
     * Create a Custom Ship with its placement, box width height, letter(data) and
     * name with the
     * orientation checked.
     * 
     * @param where        is the placement of the ship.
     * @param relativeCoor is a HashSet of the relative position of the ship to the
     *                     upperLeft corner.
     * @param w            is the with of the box of the ship.
     * @param h            is the height of the box of the ship.
     * @param letter       is the displayed letter of the ship.
     * @param name         is the name of the ship.
     * @return a CustomShip
     */
    protected Ship<Character> createCustomShip(Placement where, HashSet<Coordinate> relativeCoor, int w, int h,
            char letter, String name) {
        char o = where.getOrientation();
        checkOrientation(o, "URDL", name);
        return new CustomShip<Character>(name, where, relativeCoor, w, h, letter, '*');
    }

    private static final HashSet<Coordinate> battleship_Relative = makeBattleship_R();
    /**
     * @return Hashset of battleship relative coordinates.
     */
    private static HashSet<Coordinate> makeBattleship_R() {
        HashSet<Coordinate> s = new HashSet<>();
        s.add(new Coordinate(0, 1));
        s.add(new Coordinate(1, 0));
        s.add(new Coordinate(1, 1));
        s.add(new Coordinate(1, 2));
        return s;
    }

    private static final int Battleship_W = 3;
    private static final int Battleship_H = 2;

    private static final HashSet<Coordinate> carrier_Relative = makeCarrier_R();
    /**
     * @return HashSet of carrier relative coordinates.
     */
    private static HashSet<Coordinate> makeCarrier_R() {
        HashSet<Coordinate> s = new HashSet<>();
        s.add(new Coordinate(0, 0));
        s.add(new Coordinate(1, 0));
        s.add(new Coordinate(2, 0));
        s.add(new Coordinate(3, 0));
        s.add(new Coordinate(2, 1));
        s.add(new Coordinate(3, 1));
        s.add(new Coordinate(4, 1));
        return s;
    }

    private static final int Carrier_W = 2;
    private static final int Carrier_H = 5;
    
    /**
     * Override to create Custom Battleship.
     */
    @Override
    public Ship<Character> makeBattleship(Placement where) {
        checkOrientation(where.getOrientation(), "RUDL", "Battleship");
        return new CustomShip<Character>("Battleship", where, battleship_Relative, Battleship_W, Battleship_H, 'b',
                '*');
    }
    /**
     * Override to create Custom Carrier.
     */
    @Override
    public Ship<Character> makeCarrier(Placement where) {
        checkOrientation(where.getOrientation(), "RUDL", "Carrier");
        return new CustomShip<Character>("Carrier", where, carrier_Relative, Carrier_W, Carrier_H, 'c', '*');
    }

}
