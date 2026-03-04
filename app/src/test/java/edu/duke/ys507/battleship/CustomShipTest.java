package edu.duke.ys507.battleship;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class CustomShipTest {

    @Test
    public void test_battleship_success() {
        HashSet<Coordinate> s = new HashSet<>();
        s.add(new Coordinate(0, 1));
        s.add(new Coordinate(1, 0));
        s.add(new Coordinate(1, 1));
        s.add(new Coordinate(1, 2));
        CustomShip<Character> c1 = new CustomShip<Character>("B", new Placement("A0U"), s, 3, 2, 'b', '*');
        assertEquals(s, TestUtils.toSet(c1.getCoordinates()));
        HashSet<Coordinate> s1 = new HashSet<>();
        s1.add(new Coordinate(1, 1));
        s1.add(new Coordinate(2, 1));
        s1.add(new Coordinate(3, 1));
        s1.add(new Coordinate(2, 2));
        CustomShip<Character> c2 = new CustomShip<Character>("B", new Placement("B1R"), s, 3, 2, 'b', '*');
        assertEquals(s1, TestUtils.toSet(c2.getCoordinates()));
        HashSet<Coordinate> s2 = new HashSet<>();
        s2.add(new Coordinate(0, 0));
        s2.add(new Coordinate(0, 1));
        s2.add(new Coordinate(0, 2));
        s2.add(new Coordinate(1, 1));
        CustomShip<Character> c3 = new CustomShip<Character>("B", new Placement("A0D"), s, 3, 2, 'b', '*');
        assertEquals(s2, TestUtils.toSet(c3.getCoordinates()));
        HashSet<Coordinate> s4 = new HashSet<>();
        s4.add(new Coordinate(0, 1));
        s4.add(new Coordinate(1, 1));
        s4.add(new Coordinate(2, 1));
        s4.add(new Coordinate(1, 0));
        CustomShip<Character> c4 = new CustomShip<Character>("B", new Placement("A0L"), s, 3, 2, 'b', '*');
        assertEquals(s4, TestUtils.toSet(c4.getCoordinates()));
    }

    @Test
    public void test_carrier_success() {
        HashSet<Coordinate> s = new HashSet<>();
        s.add(new Coordinate(0, 0));
        s.add(new Coordinate(1, 0));
        s.add(new Coordinate(2, 0));
        s.add(new Coordinate(2, 1));
        s.add(new Coordinate(3, 0));
        s.add(new Coordinate(3, 1));
        s.add(new Coordinate(4, 1));

        CustomShip<Character> c1 = new CustomShip<Character>("C", new Placement("A0U"), s, 2, 5, 'c', '*');
        assertEquals(s, TestUtils.toSet(c1.getCoordinates()));
        assertEquals("C", c1.getName());
        HashSet<Coordinate> s1 = new HashSet<>();
        s1.add(new Coordinate(1, 5));
        s1.add(new Coordinate(1, 4));
        s1.add(new Coordinate(1, 3));
        s1.add(new Coordinate(2, 3));
        s1.add(new Coordinate(1, 2));
        s1.add(new Coordinate(2, 2));
        s1.add(new Coordinate(2, 1));
        CustomShip<Character> c2 = new CustomShip<Character>("C", new Placement("B1R"), s, 2, 5, 'c', '*');
        assertEquals(s1, TestUtils.toSet(c2.getCoordinates()));

        HashSet<Coordinate> s2 = new HashSet<>();
        s2.add(new Coordinate(4, 1));
        s2.add(new Coordinate(3, 1));
        s2.add(new Coordinate(2, 1));
        s2.add(new Coordinate(2, 0));
        s2.add(new Coordinate(1, 1));
        s2.add(new Coordinate(1, 0));
        s2.add(new Coordinate(0, 0));
        CustomShip<Character> c3 = new CustomShip<Character>("C", new Placement("A0D"), s, 2, 5, 'c', '*');
        assertEquals(s2, TestUtils.toSet(c3.getCoordinates()));

        HashSet<Coordinate> s3 = new HashSet<>();
        s3.add(new Coordinate(1, 0));
        s3.add(new Coordinate(1, 1));
        s3.add(new Coordinate(1, 2));
        s3.add(new Coordinate(0, 2));
        s3.add(new Coordinate(1, 3));
        s3.add(new Coordinate(0, 3));
        s3.add(new Coordinate(0, 4));
        CustomShip<Character> c4 = new CustomShip<Character>("C", new Placement("A0L"), s, 2, 5, 'c', '*');
        assertEquals(s3, TestUtils.toSet(c4.getCoordinates()));
    }

    @Test
    public void test_carrier_invalid_orientation() {
        HashSet<Coordinate> s = new HashSet<>();
        s.add(new Coordinate(0, 0));
        s.add(new Coordinate(1, 0));
        s.add(new Coordinate(2, 0));
        s.add(new Coordinate(2, 1));
        s.add(new Coordinate(3, 0));
        s.add(new Coordinate(3, 1));
        s.add(new Coordinate(4, 1));

        assertThrows(IllegalArgumentException.class, () -> {
            new CustomShip<Character>("C", new Placement("A0H"), s, 2, 5, 'c', '*');
        });
    }

}
