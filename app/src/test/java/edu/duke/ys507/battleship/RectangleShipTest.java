package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

// import org.checkerframework.checker.units.qual.radians;
import org.junit.jupiter.api.Test;

public class RectangleShipTest {
    @Test
    void test_makeCoords_1by3() {
        Coordinate ul = new Coordinate(1, 2);
        HashSet<Coordinate> actual = RectangleShip.makeCoords(ul, 1, 3);

        HashSet<Coordinate> expected = new HashSet<>();
        expected.add(new Coordinate(1, 2));
        expected.add(new Coordinate(2, 2));
        expected.add(new Coordinate(3, 2));

        assertEquals(expected, actual);
    }

    @Test
    public void test_rectangleship_1by3() {
        RectangleShip<Character> rs = new RectangleShip<Character>("submarine", new Coordinate(1, 2), 1, 3, 's', '*');
        assertEquals("submarine", rs.getName());
        assertTrue(rs.occupiesCoordinates(new Coordinate(1, 2)));
        assertTrue(rs.occupiesCoordinates(new Coordinate(2, 2)));
        assertTrue(rs.occupiesCoordinates(new Coordinate(3, 2)));
        assertFalse(rs.occupiesCoordinates(new Coordinate(0, 2)));
        assertFalse(rs.occupiesCoordinates(new Coordinate(4, 2)));
        assertFalse(rs.occupiesCoordinates(new Coordinate(1, 1)));
        assertFalse(rs.occupiesCoordinates(new Coordinate(1, 3)));
    }

    @Test
    public void test_hit_ship() {
        RectangleShip<Character> ship = new RectangleShip<Character>("submarine", new Coordinate(0, 1), 2, 3, 's', '*');
        ship.recordHitAt(new Coordinate(0, 2));
        assertTrue(ship.wasHitAt(new Coordinate(0, 2)));
        assertFalse(ship.wasHitAt(new Coordinate(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> ship.recordHitAt(new Coordinate(5, 6)));
        assertThrows(IllegalArgumentException.class, () -> ship.wasHitAt(new Coordinate(-1, 1)));
    }

    @Test
    public void test_ship_sunk() {
        RectangleShip<Character> ship = new RectangleShip<Character>("submarine", new Coordinate(0, 0), 2, 2, 's', '*');
        ship.recordHitAt(new Coordinate(0, 0));
        ship.recordHitAt(new Coordinate(0, 1));
        assertFalse(ship.isSunk());
        ship.recordHitAt(new Coordinate(1, 0));
        ship.recordHitAt(new Coordinate(1, 1));
        assertTrue(ship.isSunk());
    }

    @Test
    public void test_display_info() {
        RectangleShip<Character> ship = new RectangleShip<Character>("submarine", new Coordinate(0, 0), 2, 2, 's', '*');
        assertEquals('s', ship.getDisplayInfoAt(new Coordinate(0, 0), true));
        assertEquals(null, ship.getDisplayInfoAt(new Coordinate(0, 0), false));
        ship.recordHitAt(new Coordinate(1, 1));
        assertEquals('*', ship.getDisplayInfoAt(new Coordinate(1, 1), true));

        assertEquals('s', ship.getDisplayInfoAt(new Coordinate("B1"), false));
    }

    @Test
    public void test_get_coordinate() {
        V1ShipFactory f = new V1ShipFactory();
        Placement p = new Placement("A1v");
        Ship<Character> s = f.makeSubmarine(p);
        HashSet<Coordinate> actual = new HashSet<>();
        for (Coordinate c : s.getCoordinates()) {
            actual.add(c);
        }
        HashSet<Coordinate> expect = new HashSet<>();
        expect.add(new Coordinate("A1"));
        expect.add(new Coordinate("B1"));
        assertEquals(expect, actual);
    }
}
