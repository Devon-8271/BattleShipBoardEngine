package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class PlayerActionTest {
    @Test
    public void test_playeraction_domove_success() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Submarine", (p) -> f.makeSubmarine(p));
        m.put("Destroyer", (p) -> f.makeDestroyer(p));
        m.put("Battleship", (p) -> f.makeBattleship(p));
        m.put("Carrier", (p) -> f.makeCarrier(p));

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        b.fireAt(new Coordinate("B1"));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertNull(err);
        assertNull(b.getShipAt(new Coordinate(1, 1)));
        Ship<Character> newship = b.getShipAt(new Coordinate(4, 5));
        assertNotNull(newship);

        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(4, 5)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(4, 5)));
    }

    @Test
    public void test_playeraction_domove_no_ship_at_coordinate() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        String err = a.doMove(b, m, new Coordinate(0, 0), new Placement("D4U"));
        assertNotNull(err);
    }

    @Test
    public void test_playerAction_doMove_fail_add_and_rollback() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));
        m.put("Carrier", (p) -> f.makeCarrier(p));

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        assertNull(b.tryAddShip(f.makeCarrier(new Placement("D4U"))));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertNotNull(err);

        assertNotNull(b.getShipAt(new Coordinate(1, 1)));
        assertNotNull(b.getShipAt(new Coordinate(3, 4)));
    }

    @Test
    public void test_playerAction_doMove_fail_add_rollback_preserves_damage() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));
        m.put("Carrier", (p) -> f.makeCarrier(p));

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        assertNull(b.tryAddShip(f.makeCarrier(new Placement("D4U"))));

        b.fireAt(new Coordinate(1, 1));
        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(1, 1)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(1, 1)));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertNotNull(err);

        assertNotNull(b.getShipAt(new Coordinate(1, 1)));
        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(1, 1)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(1, 1)));

        assertNotNull(b.getShipAt(new Coordinate(3, 4)));
    }

    @Test
    public void test_playerAction_doMove_unknown_ship_type() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Carrier", (p) -> f.makeCarrier(p));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertEquals("Unknown ship type\n", err);
    }

    @Test
    public void test_playerAction_doMove_invalid_newPlacement_format() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        Placement bad = new Placement(new Coordinate(0, 0), 'H');
        String err = a.doMove(b, m, new Coordinate(1, 1), bad);

        assertEquals("That placement is invalid: it does not have the correct format\n", err);
        assertNotNull(b.getShipAt(new Coordinate(1, 1)));
    }

    @Test
    public void test_playerAction_doMove_removeShip_fails() {
        class BadRemoveBoard extends BattleShipBoard<Character> {
            public BadRemoveBoard(int w, int h, Character miss) {
                super(w, h, miss);
            }

            @Override
            public boolean removeShip(Ship<Character> s) {
                return false;
            }
        }

        BadRemoveBoard b = new BadRemoveBoard(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertEquals("Could not remove ship from the board\n", err);

        assertNotNull(b.getShipAt(new Coordinate(1, 1)));
    }

    @Test
    public void test_move_preserves_enemy_hit_marker_at_old_location() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        b.fireAt(new Coordinate(1, 1));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(1, 1)));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertNull(err);

        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(1, 1)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(4, 5)));
        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(4, 5)));
    }

    @Test
    public void test_move_preserves_enemy_miss_marker_at_new_location() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        PlayerAction a = new PlayerAction();

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        b.fireAt(new Coordinate(4, 5));
        assertEquals(Character.valueOf('X'), b.whatIsAtForEnemy(new Coordinate(4, 5)));

        String err = a.doMove(b, m, new Coordinate(1, 1), new Placement("D4U"));
        assertNull(err);

        assertEquals(Character.valueOf('X'), b.whatIsAtForEnemy(new Coordinate(4, 5)));
    }

    @Test
    public void test_move_copies_damage_with_rotation_rectangle() {
        PlayerAction action = new PlayerAction();
        Board<Character> b = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory f = new V2ShipFactory();

        Ship<Character> oldS = f.makeDestroyer(new Placement("A0H"));
        assertNull(b.tryAddShip(oldS));

        b.fireAt(new Coordinate("A0"));
        b.fireAt(new Coordinate("A2"));

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Destroyer", (p) -> f.makeDestroyer(p));

        String err = action.doMove(b, m, new Coordinate("A0"), new Placement("L6V"));
        assertNull(err);

        Ship<Character> newS = b.getShipAt(new Coordinate("L6"));
        assertNotNull(newS);

        assertTrue(newS.wasHitAt(new Coordinate("L6")));
        assertTrue(newS.wasHitAt(new Coordinate("N6")));
    }

    @Test
    public void test_move_copies_damage_with_rotation_customship() {
        PlayerAction action = new PlayerAction();
        Board<Character> b = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory f = new V2ShipFactory();

        Ship<Character> oldS = f.makeBattleship(new Placement("A0U"));
        b.tryAddShip(oldS);

        Coordinate h1 = oldS.getCoordinates().iterator().next();
        b.fireAt(h1);

        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));

        action.doMove(b, m, h1, new Placement("L6R"));

        Ship<Character> newS = b.getShipAt(new Coordinate("L6"));

        // This assertion triggers the false branch of "if (newS == null)"
        assertNotNull(newS);

        int hits = 0;
        for (Coordinate c : newS.getCoordinates()) {
            if (newS.wasHitAt(c))
                hits++;
        }
        assertEquals(1, hits);
    }

    @Test
    public void test_copyDamage_geometry_mismatch_throws() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        Ship<Character> oldShip = f.makeDestroyer(new Placement("A0H"));
        Ship<Character> newShip = f.makeSubmarine(new Placement("B0H"));

        PlayerAction action = new PlayerAction();

        Method m = PlayerAction.class.getDeclaredMethod("copyDamage", Ship.class, Ship.class);
        m.setAccessible(true);

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(action, oldShip, newShip);
            } catch (Exception e) {
                throw (RuntimeException) e.getCause();
            }
        });
    }

    @Test
    public void test_copyDamage_loops_all_4_then_throws() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        Ship<Character> oldShip = f.makeDestroyer(new Placement("A0H"));
        Ship<Character> newShip = f.makeSubmarine(new Placement("B0H"));

        PlayerAction action = new PlayerAction();
        Method m = PlayerAction.class.getDeclaredMethod("copyDamage", Ship.class, Ship.class);
        m.setAccessible(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            try {
                m.invoke(action, oldShip, newShip);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(ex.getCause() != null);
        assertTrue(ex.getCause().getCause() instanceof IllegalArgumentException);
    }

    @Test
    public void test_rot_covers_k2_k3() throws Exception {
        PlayerAction a = new PlayerAction();

        Class<?> pc = Class.forName("edu.duke.ys507.battleship.PlayerAction$P");
        Constructor<?> ctor = pc.getDeclaredConstructor(int.class, int.class);
        ctor.setAccessible(true);
        Object p = ctor.newInstance(2, 5);

        Method rot = PlayerAction.class.getDeclaredMethod("rot", pc, int.class);
        rot.setAccessible(true);

        assertNotNull(rot.invoke(a, p, 2));
        assertNotNull(rot.invoke(a, p, 3));
    }

    @Test
    public void test_P_equals_nonP_returns_false() throws Exception {
        Class<?> pc = Class.forName("edu.duke.ys507.battleship.PlayerAction$P");
        Constructor<?> ctor = pc.getDeclaredConstructor(int.class, int.class);
        ctor.setAccessible(true);
        Object p = ctor.newInstance(1, 1);

        assertFalse(p.equals("x"));
    }

}
