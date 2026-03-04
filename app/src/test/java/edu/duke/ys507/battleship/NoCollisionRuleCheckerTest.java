package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NoCollisionRuleCheckerTest {
    @Test
    public void test_no_collision(){
        V1ShipFactory f = new V1ShipFactory();
        Placement p = new Placement("A1h");
        Placement p1 = new Placement("A1v");
        Placement p2 = new Placement("C4v");
        Ship<Character> s1 = f.makeSubmarine(p);
        Ship<Character> s2 = f.makeBattleship(p1);
        Ship<Character> s4 = f.makeDestroyer(p2);
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(5, 10, 'X');
        NoCollisionRuleChecker<Character> checker = new NoCollisionRuleChecker<>(null);
        assertEquals(null, checker.checkMyRule(s1, b));
        b.tryAddShip(s1);
        assertEquals("That placement is invalid: the ship overlaps another ship."
, checker.checkMyRule(s2, b));
        assertEquals(null, checker.checkMyRule(s4, b));
    }

    @Test
    public void test_chain_checker(){
        V1ShipFactory f = new V1ShipFactory();
        Placement p = new Placement("A1h");
        Placement p1 = new Placement("A1v");
        Placement p2 = new Placement("Y4v");
        Placement p3 = new Placement("M6v");
        Ship<Character> s1 = f.makeSubmarine(p);
        Ship<Character> s2 = f.makeBattleship(p1);
        Ship<Character> s3 = f.makeCarrier(p3);
        Ship<Character> s4 = f.makeDestroyer(p2);
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(5, 10, 'X');
        PlacementRuleChecker<Character> checker = new NoCollisionRuleChecker<>(new InBoundsRuleChecker<Character>(null));
        assertEquals(null, checker.checkPlacement(s1, b));
        b.tryAddShip(s1);
        assertEquals("That placement is invalid: the ship overlaps another ship.", checker.checkPlacement(s2, b));
        assertEquals("That placement is invalid: the ship goes off the bottom of the board.", checker.checkPlacement(s3, b));
        assertEquals("That placement is invalid: the ship goes off the bottom of the board.", checker.checkPlacement(s4, b));

    }

    
}
