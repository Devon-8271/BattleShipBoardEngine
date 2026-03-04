package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SimpleShipDisplayInfoTest {
    @Test
    public void test_getinfo(){
        ShipDisplayInfo<Character> info = new SimpleShipDisplayInfo<>('s', '*');
        assertEquals('s', info.getInfo(new Coordinate(0, 0), false));
        assertEquals('*', info.getInfo(new Coordinate(1, 1), true));

    }
    
}