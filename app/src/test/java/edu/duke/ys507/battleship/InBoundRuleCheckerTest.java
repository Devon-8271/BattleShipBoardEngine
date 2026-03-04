package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InBoundRuleCheckerTest {
    @Test
    public void test_inbound_check(){
        V1ShipFactory f = new V1ShipFactory();
        Placement p = new Placement("A1h");
        Placement p1 = new Placement(new Coordinate(10, 0), 'h');
        Ship<Character> s1 = f.makeSubmarine(p);//A1, A2
        Ship<Character> s2 = f.makeBattleship(new Placement(new Coordinate(-1, 2), 'h'));
        Ship<Character> s3 = f.makeSubmarine(p1);//J0, J1
        Ship<Character> s4 = f.makeDestroyer(new Placement(new Coordinate(1, -1), 'v'));
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 5, 'X');
        BattleShipBoard<Character> b1 = new BattleShipBoard<Character>(8, 11, 'X');
        InBoundsRuleChecker<Character> checker = new InBoundsRuleChecker<>(null);
        assertEquals("That placement is invalid: the ship goes off the right of the board.", checker.checkMyRule(s1, b));
        assertEquals("That placement is invalid: the ship goes off the top of the board.", checker.checkMyRule(s2, b1));
        assertEquals(null, checker.checkMyRule(s3, b1));
        assertEquals("That placement is invalid: the ship goes off the left of the board.", checker.checkMyRule(s4, b1));
        assertEquals(null, checker.checkPlacement(s3, b1));
        assertEquals("That placement is invalid: the ship goes off the bottom of the board.", checker.checkPlacement(s3, b));
    }

    @Test
    public void test_checker_constructor(){
        PlacementRuleChecker<Character> checker = new InBoundsRuleChecker<>(null);
        V1ShipFactory f = new V1ShipFactory();
        Placement p = new Placement("A0h");
        Ship<Character> s1 = f.makeSubmarine(p);
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 5, checker, 'X');
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<>(-1, 20, null));
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<>(1, -20, null));
        assertEquals(null, checker.checkMyRule(s1, b));
    }
}
