package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class V1ShipFactoryTest {
    private void checkShip(Ship<Character> testShip, String expectedName,
            char expectedLetter, Coordinate... expectedLocs) {
        assertEquals(expectedName, testShip.getName());
        assertEquals(expectedLetter, testShip.getDisplayInfoAt((expectedLocs[0]), true));
        for (Coordinate c : expectedLocs) {
            assertTrue(testShip.occupiesCoordinates(c));
        }
    }

    @Test
    public void test_create_ships(){
        V1ShipFactory f = new V1ShipFactory();
        Placement v1_2 = new Placement(new Coordinate(1, 2), 'V');
        Ship<Character> s1 = f.makeDestroyer(v1_2);
        checkShip(s1, "Destroyer", 'd', new Coordinate(1, 2), new Coordinate(2, 2), new Coordinate(3, 2));

        Ship<Character> s2 = f.makeCarrier(v1_2);
        checkShip(s2, "Carrier", 'c', new Coordinate(1, 2), new Coordinate(2, 2), new Coordinate(3, 2), new Coordinate(4, 2), new Coordinate(5, 2), new Coordinate(6, 2));

        Placement h1_2 = new Placement(new Coordinate(1, 2), 'h');
        Ship<Character> s3 = f.makeSubmarine(h1_2);
        checkShip(s3, "Submarine", 's', new Coordinate(1, 2), new Coordinate(1, 3));

        Ship<Character> s4 = f.makeBattleship(h1_2);
        checkShip(s4, "Battleship", 'b', new Coordinate(1, 2), new Coordinate(1, 3), new Coordinate(1, 4), new Coordinate(1, 5));

    }
    @Test
    public void test_invalid_orientation(){
        V1ShipFactory f = new V1ShipFactory();
        assertThrows(IllegalArgumentException.class, () ->f.makeBattleship(new Placement("A1L")));
    }
}
