package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import static edu.duke.ys507.battleship.TestUtils.toSet;

import org.junit.jupiter.api.Test;

public class V2ShipFactoryTest {
    @Test
    public void test_v2factory_battleship() {
        V2ShipFactory f = new V2ShipFactory();

        HashSet<Coordinate> u = new HashSet<>();
        u.add(new Coordinate(0, 1));
        u.add(new Coordinate(1, 0));
        u.add(new Coordinate(1, 1));
        u.add(new Coordinate(1, 2));
        assertEquals(u, toSet(f.makeBattleship(new Placement("A0U")).getCoordinates()));

        HashSet<Coordinate> r = new HashSet<>();
        r.add(new Coordinate(0, 0));
        r.add(new Coordinate(1, 0));
        r.add(new Coordinate(2, 0));
        r.add(new Coordinate(1, 1));
        assertEquals(r, toSet(f.makeBattleship(new Placement("A0R")).getCoordinates()));

        HashSet<Coordinate> d = new HashSet<>();
        d.add(new Coordinate(0, 0));
        d.add(new Coordinate(0, 1));
        d.add(new Coordinate(0, 2));
        d.add(new Coordinate(1, 1));
        assertEquals(d, toSet(f.makeBattleship(new Placement("A0D")).getCoordinates()));

        HashSet<Coordinate> l = new HashSet<>();
        l.add(new Coordinate(0, 1));
        l.add(new Coordinate(1, 1));
        l.add(new Coordinate(2, 1));
        l.add(new Coordinate(1, 0));
        assertEquals(l, toSet(f.makeBattleship(new Placement("A0L")).getCoordinates()));
    }

    @Test
    public void test_v2factory_carrier() {
        V2ShipFactory f = new V2ShipFactory();

        HashSet<Coordinate> u = new HashSet<>();
        u.add(new Coordinate(0, 0));
        u.add(new Coordinate(1, 0));
        u.add(new Coordinate(2, 0));
        u.add(new Coordinate(2, 1));
        u.add(new Coordinate(3, 0));
        u.add(new Coordinate(3, 1));
        u.add(new Coordinate(4, 1));
        assertEquals(u, toSet(f.makeCarrier(new Placement("A0U")).getCoordinates()));

        HashSet<Coordinate> r = new HashSet<>();
        r.add(new Coordinate(0, 4));
        r.add(new Coordinate(0, 3));
        r.add(new Coordinate(0, 2));
        r.add(new Coordinate(1, 2));
        r.add(new Coordinate(0, 1));
        r.add(new Coordinate(1, 1));
        r.add(new Coordinate(1, 0));
        assertEquals(r, toSet(f.makeCarrier(new Placement("A0R")).getCoordinates()));

        HashSet<Coordinate> d = new HashSet<>();
        d.add(new Coordinate(0, 0));
        d.add(new Coordinate(1, 0));
        d.add(new Coordinate(1, 1));
        d.add(new Coordinate(2, 0));
        d.add(new Coordinate(2, 1));
        d.add(new Coordinate(3, 1));
        d.add(new Coordinate(4, 1));
        assertEquals(d, toSet(f.makeCarrier(new Placement("A0D")).getCoordinates()));

        HashSet<Coordinate> l = new HashSet<>();
        l.add(new Coordinate(0, 4));
        l.add(new Coordinate(0, 3));
        l.add(new Coordinate(0, 2));
        l.add(new Coordinate(1, 3));
        l.add(new Coordinate(1, 2));
        l.add(new Coordinate(1, 1));
        l.add(new Coordinate(1, 0));
        assertEquals(l, toSet(f.makeCarrier(new Placement("A0L")).getCoordinates()));
    }

    @Test
    public void test_v2factory_invalid_orientation() {
        V2ShipFactory f = new V2ShipFactory();
        assertThrows(IllegalArgumentException.class, () -> f.makeBattleship(new Placement("A0H")));
        assertThrows(IllegalArgumentException.class, () -> f.makeBattleship(new Placement("A0V")));
        assertThrows(IllegalArgumentException.class, () -> f.makeCarrier(new Placement("A0H")));
        assertThrows(IllegalArgumentException.class, () -> f.makeCarrier(new Placement("A0V")));
    }

    @Test
    public void test_createCustomShip() {
        class V2ShipFactoryExpose extends V2ShipFactory {
            public Ship<Character> callCreateCustomShip(Placement where, HashSet<Coordinate> relativeCoor,
                    int w, int h, char letter, String name) {
                return createCustomShip(where, relativeCoor, w, h, letter, name);
            }
        }

        V2ShipFactoryExpose f = new V2ShipFactoryExpose();

        HashSet<Coordinate> relB = new HashSet<>();
        relB.add(new Coordinate(0, 1));
        relB.add(new Coordinate(1, 0));
        relB.add(new Coordinate(1, 1));
        relB.add(new Coordinate(1, 2));

        HashSet<Coordinate> expU = new HashSet<>();
        expU.add(new Coordinate(0, 1));
        expU.add(new Coordinate(1, 0));
        expU.add(new Coordinate(1, 1));
        expU.add(new Coordinate(1, 2));
        Ship<Character> sU = f.callCreateCustomShip(new Placement("A0U"), relB, 3, 2, 'b', "Battleship");
        assertEquals(expU, toSet(sU.getCoordinates()));

        HashSet<Coordinate> expR = new HashSet<>();
        expR.add(new Coordinate(0, 0));
        expR.add(new Coordinate(1, 0));
        expR.add(new Coordinate(2, 0));
        expR.add(new Coordinate(1, 1));
        Ship<Character> sR = f.callCreateCustomShip(new Placement("A0R"), relB, 3, 2, 'b', "Battleship");
        assertEquals(expR, toSet(sR.getCoordinates()));

        HashSet<Coordinate> expD = new HashSet<>();
        expD.add(new Coordinate(0, 0));
        expD.add(new Coordinate(0, 1));
        expD.add(new Coordinate(0, 2));
        expD.add(new Coordinate(1, 1));
        Ship<Character> sD = f.callCreateCustomShip(new Placement("A0D"), relB, 3, 2, 'b', "Battleship");
        assertEquals(expD, toSet(sD.getCoordinates()));

        HashSet<Coordinate> expL = new HashSet<>();
        expL.add(new Coordinate(0, 1));
        expL.add(new Coordinate(1, 0));
        expL.add(new Coordinate(1, 1));
        expL.add(new Coordinate(2, 1));
        Ship<Character> sL = f.callCreateCustomShip(new Placement("A0L"), relB, 3, 2, 'b', "Battleship");
        assertEquals(expL, toSet(sL.getCoordinates()));

        assertThrows(IllegalArgumentException.class, () -> {
            f.callCreateCustomShip(new Placement("A0H"), relB, 3, 2, 'b', "Battleship");
        });
    }

    @Test
    public void test_v2_borders_reject_carrier_out_of_bounds() {
        Board<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        Ship<Character> c = f.makeCarrier(new Placement("A9U"));
        assertNotNull(b.tryAddShip(c));
    }

    @Test
    public void test_v2_overlap_reject() {
        Board<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();

        Ship<Character> c1 = f.makeCarrier(new Placement("A0U"));
        assertNull(b.tryAddShip(c1));

        Ship<Character> b2 = f.makeBattleship(new Placement("A0U"));
        assertNotNull(b.tryAddShip(b2));
    }

    @Test
    public void test_v2_display_and_hit_markers() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        Ship<Character> bs = f.makeBattleship(new Placement("A0U"));
        assertEquals(null,b.tryAddShip(bs));

        assertEquals(Character.valueOf('b'), b.whatIsAtForSelf(new Coordinate(0, 1)));
        assertEquals(null,b.whatIsAtForEnemy(new Coordinate(0, 1)));

        b.fireAt(new Coordinate(0, 1));

        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(0, 1)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(0, 1)));
    }
}