package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PlacementTest {
    @Test
    public void test_coord_and_orien(){
        Coordinate c1 = new Coordinate(3, 2);
        Placement p1 = new Placement(c1, 'v');
        assertEquals(c1, p1.getCoordinate());
        assertEquals('V', p1.getOrientation());
    }

    @Test
    public void test_equals(){
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(1, 3);
        Placement p1 = new Placement(c1, 'v');
        Placement p2 = new Placement(c1, 'v');
        Placement p3 = new Placement(c1, 'h');
        Placement p4 = new Placement(c2, 'v');
        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p3, p4);
        assertNotEquals(p2, p4);
        assertNotEquals(p1, c1);
    }

    @Test
    public void test_hashCode(){
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(1, 3);
        Placement p1 = new Placement(c1, 'v');
        Placement p2 = new Placement(c1, 'V');
        Placement p3 = new Placement(c1, 'h');
        Placement p4 = new Placement(c2, 'v');
        assertEquals(p1.hashCode(), p1.hashCode());
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
        assertNotEquals(p3.hashCode(), p4.hashCode());
    }

    @Test
    void test_string_constructor_valid_cases(){
        Coordinate c1 = new Coordinate(0, 0);
        Coordinate c2 = new Coordinate(1, 9);
        Coordinate c3 = new Coordinate(0, 2);
        Coordinate c4 = new Coordinate(5, 5);
        Placement p1 = new Placement("A0V");
        Placement p2 = new Placement("B9h");
        Placement p3 = new Placement("A2v");
        Placement p4 = new Placement("F5H");
        assertEquals(c1, p1.getCoordinate());
        assertEquals('V', p1.getOrientation());
        assertEquals(c2, p2.getCoordinate());
        assertEquals('H', p2.getOrientation());
        assertEquals(c3, p3.getCoordinate());
        assertEquals('V', p3.getOrientation());
        assertEquals(c4, p4.getCoordinate());
        assertEquals('H', p4.getOrientation());
    }

    @Test
    void test_string_constructor_error_cases(){
        assertThrows(IllegalArgumentException.class, () -> new Placement("AAAA"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("AA"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("AAv"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("A*h"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("11h"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("A12"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("A0b"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("F9K"));
        assertThrows(IllegalArgumentException.class, () -> new Placement("A0?"));
    }
}
