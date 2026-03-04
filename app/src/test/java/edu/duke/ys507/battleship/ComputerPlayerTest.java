package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class ComputerPlayerTest {
    @Test
    public void test_computer_doPlacementPhaseV2_places_some_ships_using_getShipAt() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        Board<Character> b = new BattleShipBoard<>(10, 20, 'X');

        ComputerPlayer cp = new ComputerPlayer(
                b,
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                f,
                20,
                10);

        cp.doPlacementPhaseV2();

        Set<Ship<Character>> ships = new HashSet<>();
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 10; c++) {
                Ship<Character> s = b.getShipAt(new Coordinate(r, c));
                if (s != null) {
                    ships.add(s);
                }
            }
        }
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 10; c++) {
                Ship<Character> s = b.getShipAt(new Coordinate(r, c));
                if (s != null) {
                    ships.add(s);
                }
            }
        }
        int subs = 0, dest = 0, battles = 0, carriers = 0;
        for (Ship<Character> s : ships) {
            String n = s.getName();
            if (n.equals("Submarine"))
                subs++;
            else if (n.equals("Destroyer"))
                dest++;
            else if (n.equals("Battleship"))
                battles++;
            else
                carriers++;

        }

        assertEquals(2, subs);
        assertEquals(3, dest);
        assertEquals(3, battles);
        assertEquals(2, carriers);
    }

    @Test
    public void test_computer_doPlacementPhaseV2_places_all_four_types() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');

        ComputerPlayer cp = new ComputerPlayer(
                b,
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                f,
                10,
                10);

        cp.doPlacementPhaseV2();

        java.util.Set<String> names = new java.util.HashSet<>();
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 10; c++) {
                Ship<Character> s = b.getShipAt(new Coordinate(r, c));
                if (s != null) {
                    names.add(s.getName());
                }
            }
        }

        assertTrue(names.contains("Submarine"));
        assertTrue(names.contains("Destroyer"));
        assertTrue(names.contains("Battleship"));
        assertTrue(names.contains("Carrier"));
    }

    @Test
    public void test_computer_getters() {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');

        ComputerPlayer cp = new ComputerPlayer(b, new PrintStream(new ByteArrayOutputStream()), "A", f, 10, 20);

        assertEquals("A", cp.getName());
        assertNotNull(cp.getBoard());
        assertNotNull(cp.getView());
    }

    @Test
    public void test_computer_announceWin_prints_message() {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ComputerPlayer cp = new ComputerPlayer(b, new PrintStream(bytes), "B", f, 10, 20);

        cp.announceWin();

        String out = bytes.toString();
        assertTrue(out.contains("PLAYER B WINS"));
    }

    @Test
    public void test_computer_playOneTurnV2_does_not_repeat_coordinates() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> aBoard = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> bBoard = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bytes);

        ComputerPlayer a = new ComputerPlayer(aBoard, ps, "A", f, 10, 20);
        ComputerPlayer b = new ComputerPlayer(bBoard, ps, "B", f, 10, 20);

        a.playOneTurnV2(bBoard, new BoardTextView(bBoard), "B");
        a.playOneTurnV2(bBoard, new BoardTextView(bBoard), "B");
        b.playOneTurnV2(aBoard, new BoardTextView(aBoard), "A");

        String[] lines = bytes.toString().split("\\R");
        java.util.List<String> actionLines = new java.util.ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("Player A ")) {
                actionLines.add(line);
            }
        }
        assertTrue(actionLines.size() >= 2);
        assertNotEquals(actionLines.get(0), actionLines.get(1));
    }

    @Test
    public void test_computer_playOneTurnV2_prints_miss_or_hit() throws IOException {
        V2ShipFactory f = new V2ShipFactory();

        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ComputerPlayer cp = new ComputerPlayer(my, new PrintStream(bytes), "A", f, 10, 20);

        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String out = bytes.toString();
        assertTrue(out.contains("Player A "),
                "Should print an outcome line");
        assertTrue(out.contains("hit") || out.contains("missed"),
                "Should say hit or missed");
    }

    @Test
    public void test_computer_scan_wraps_to_next_row_width20() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10);

        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        for (int i = 0; i < 21; i++) {
            cp.playOneTurnV2(enemy, enemyView, "B");
        }

        assertTrue(bytes.toString().contains("B0"));
    }

    @Test
    public void test_computer_last_shot_on_10x20_is_T9() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return false;
            }
        };
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        int total = 10 * 20;
        for (int i = 0; i < total; i++) {
            cp.playOneTurnV2(enemy, enemyView, "B");
        }

        String[] lines = bytes.toString().trim().split("\\R");
        String last = lines[lines.length - 1];
        assertTrue(last.contains("T9"));
    }

    @Test
    public void test_computer_first_two_shots_follow_scan_order() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10);

        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        cp.playOneTurnV2(enemy, enemyView, "B");
        cp.playOneTurnV2(enemy, enemyView, "B");

        String s = bytes.toString();
        assertTrue(s.contains("A0"));
        assertTrue(s.contains("A1"));
    }

    @Test
    public void test_computer_doPlacementPhase_delegates_to_v2() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');

        Player p = new ComputerPlayer(
                b,
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                f,
                20,
                10);

        p.doPlacementPhase();

        int occupied = 0;
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 10; c++) {
                if (b.getShipAt(new Coordinate(r, c)) != null) {
                    occupied++;
                }
            }
        }
        assertTrue(occupied > 0);
    }

    @Test
    public void test_computer_playOneTurn_delegates_to_v2_output_has_player_name() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        Board<Character> self = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(self, out, "A", f, 10, 20);
        cp.playOneTurn(enemy, new BoardTextView(enemy), "B");

        String s = bytes.toString();
        assertTrue(s.contains("Player A"), "Expected output to mention Player A, got:\n" + s);
    }

    @Test
    public void test_computer_special_action_prints_only_header() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        Board<Character> self = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(self, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };

        cp.doPlacementPhaseV2();
        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertTrue(bytes.toString().contains("Player A used a special action"));
    }

    @Test
    public void test_computer_special_sonar_path() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        Board<Character> my = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertTrue(bytes.toString().contains("used a special action"));
    }

    static class TestComputerPlayer extends ComputerPlayer {
        boolean forceSpecial = false;
        boolean forceCoin = true;
        Boolean forcedMoveResult = null;

        TestComputerPlayer(Board<Character> b, PrintStream out, String name,
                AbstractShipFactory<Character> factory, int height, int width) {
            super(b, out, name, factory, height, width);
        }

        @Override
        protected boolean shoudUseSpecialAction() {
            return forceSpecial;
        }

        @Override
        protected boolean coinFlip() {
            return forceCoin;
        }

        @Override
        protected boolean tryRandomMove() {
            if (forcedMoveResult != null)
                return forcedMoveResult.booleanValue();
            return super.tryRandomMove();
        }
    }

    @Test
    public void test_computer_special_move_path_tryRandomMove_false() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 10, 20) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }

            @Override
            protected boolean tryRandomMove() {
                return false;
            }

            @Override
            protected boolean coinFlip() {
                return false;
            }
        };

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertTrue(bytes.toString().contains("used a special action"));
    }

    @Test
    public void test_tryRandomMove_no_ship_returns_false() {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TestComputerPlayer cp = new TestComputerPlayer(my, out, "A", f, 10, 20);
        assertFalse(cp.tryRandomMove());
    }

    @Test
    public void test_special_both_available_choose_sonar_prints_header_only() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TestComputerPlayer cp = new TestComputerPlayer(my, out, "A", f, 10, 20);
        cp.forceSpecial = true;
        cp.forceCoin = true;

        cp.playOneTurnV2(enemy, enemyView, "B");

        assertEquals("Player A used a special action", bytes.toString().trim());
    }

    @Test
    public void test_special_both_available_choose_move_prints_header_only() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TestComputerPlayer cp = new TestComputerPlayer(my, out, "A", f, 10, 20);
        cp.forceSpecial = true;
        cp.forceCoin = false;
        cp.forcedMoveResult = Boolean.TRUE;

        cp.playOneTurnV2(enemy, enemyView, "B");

        assertEquals("Player A used a special action", bytes.toString().trim());
    }

    @Test
    public void test_special_only_sonar_available_prints_header_only() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TestComputerPlayer cp = new TestComputerPlayer(my, out, "A", f, 10, 20);
        cp.forceSpecial = true;
        setInt(cp, "moveRemaining", 0);

        cp.playOneTurnV2(enemy, enemyView, "B");

        assertEquals("Player A used a special action", bytes.toString().trim());
    }

    private static void setInt(Object obj, String field, int v) {
        try {
            Class<?> c = obj.getClass();
            Field f = null;

            while (c != null && f == null) {
                try {
                    f = c.getDeclaredField(field);
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }

            if (f == null) {
                throw new NoSuchFieldException(field);
            }

            f.setAccessible(true);
            f.setInt(obj, v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_setInt_bad_field_throws() {
        V2ShipFactory f = new V2ShipFactory();
        Board<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);
        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 10, 20);

        assertThrows(RuntimeException.class, () -> setInt(cp, "notARealField", 1));
    }

    static int getInt(Object obj, String field) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.getInt(obj);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field not found: " + field);
    }

    @Test
    public void test_special_action_move_only_tryRandomMove_fails() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        Board<Character> my = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        my.tryAddShip(f.makeDestroyer(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }

            @Override
            protected boolean tryRandomMove() {
                return false;
            }

        };

        setInt(cp, "sonarRemaining", 0);
        int before = getInt(cp, "moveRemaining");

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String s = bytes.toString();
        assertTrue(s.contains("used a special action"));
        assertFalse(s.contains("missed"));

        int after = getInt(cp, "moveRemaining");
        assertEquals(before, after);
    }

    @Test
    public void test_special_move_attempt_fails_still_returns_after_header() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TestComputerPlayer cp = new TestComputerPlayer(my, out, "A", f, 10, 20);
        cp.forceSpecial = true;
        cp.forceCoin = false;
        cp.forcedMoveResult = Boolean.FALSE;

        cp.playOneTurnV2(enemy, enemyView, "B");

        assertEquals("Player A used a special action", bytes.toString().trim());
    }

    @Test
    public void test_playOneTurnV2_move_only() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');
        b.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ComputerPlayer cp = new ComputerPlayer(b, new PrintStream(bytes), "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };

        try {
            Field sonarF = ComputerPlayer.class.getDeclaredField("sonarRemaining");
            sonarF.setAccessible(true);
            sonarF.set(cp, 0);

            Field moveF = ComputerPlayer.class.getDeclaredField("moveRemaining");
            moveF.setAccessible(true);
            moveF.set(cp, 3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cp.playOneTurnV2(b, new BoardTextView(b), "Enemy");

        String out = bytes.toString();
        assertTrue(out.contains("Player A used a special action"));
    }

    static class AlwaysFailMoveComputerPlayer extends ComputerPlayer {
        AlwaysFailMoveComputerPlayer(Board<Character> b, PrintStream out, String name,
                AbstractShipFactory<Character> factory, int height, int width) {
            super(b, out, name, factory, height, width);
        }

        @Override
        protected Placement randomPlacementForShipMove() {
            return new Placement("Z9H");
        }
    }

    @Test
    public void test_tryRandomMove_all_tries_fail_returns_false() throws Exception {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        AlwaysFailMoveComputerPlayer cp = new AlwaysFailMoveComputerPlayer(my, out, "A", f, 10, 20);

        assertFalse(cp.tryRandomMove());
    }

    @Test
    public void test_computer_special_move_path() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 10, 20) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }

        };

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertTrue(bytes.toString().contains("used a special action"));
    }

    @Test
    public void test_randomPlacementForShipMove_uses_existing_ship_name_branch() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        Board<Character> my = new BattleShipBoard<>(10, 20, 'X');

        my.tryAddShip(f.makeDestroyer(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected Random makeRng() {
                return new Random() {
                    private int call = 0;

                    @Override
                    public int nextInt(int bound) {
                        call++;
                        if (call == 1)
                            return 0;
                        else {
                            return 0;
                        }
                    }

                    @Override
                    public boolean nextBoolean() {
                        return true;
                    }
                };
            }
        };

        Placement p = cp.randomPlacementForShipMove();
        assertNotNull(p);
    }

    @Test
    public void test_special_action_but_none_available_falls_back_to_fire() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        Board<Character> my = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };

        setInt(cp, "sonarRemaining", 0);
        setInt(cp, "moveRemaining", 0);

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String s = bytes.toString();
        assertFalse(s.contains("used a special action"));
        assertTrue(s.contains("missed") || s.contains("hit"));
    }

    @Test
    public void test_computer_special_move_path_covers_tryRandomMove() throws IOException {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        my.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }

            @Override
            protected boolean tryRandomMove() {
                return false;
            }
        };
        setInt(cp, "sonarRemaining", 0);

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        assertTrue(bytes.toString().contains("used a special action"));
    }

    @Test
    public void test_special_requested_but_no_sonar_no_move_falls_back_to_fire_covers_canMove_false() throws Exception {
        AbstractShipFactory<Character> f = new V2ShipFactory();
        BattleShipBoard<Character> my = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(10, 20, 'X');

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        ComputerPlayer cp = new ComputerPlayer(my, out, "A", f, 20, 10) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }

            @Override
            protected Coordinate nextFireAt() {
                return new Coordinate(0, 0);
            }
        };

        setInt(cp, "sonarRemaining", 0);
        setInt(cp, "moveRemaining", 0);

        cp.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertTrue(bytes.toString().contains("missed at A0"));
        assertFalse(bytes.toString().contains("used a special action"));
    }

    @Test
    public void test_tryRandomMove_fails_when_all_attempts_fail() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');

        b.tryAddShip(f.makeSubmarine(new Placement("A0H")));

        ComputerPlayer cp = new ComputerPlayer(b, new PrintStream(new ByteArrayOutputStream()), "A", f, 10, 20) {
            @Override
            protected Placement randomPlacementForShipMove() {
                return new Placement("Z9V");
            }
        };

        boolean result = cp.tryRandomMove();
        assertFalse(result);
    }

    @Test
    public void test_getInt_field_not_found() {
        V2ShipFactory f = new V2ShipFactory();
        assertThrows(RuntimeException.class, () -> {
            getInt(f, "nonExistentField");
        });
    }

    @Test
    public void test_playOneTurnV2_no_actions_left() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ComputerPlayer cp = new ComputerPlayer(b, new PrintStream(bytes), "A", f, 10, 20) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };

        try {
            Field sonarF = ComputerPlayer.class.getDeclaredField("sonarRemaining");
            Field moveF = ComputerPlayer.class.getDeclaredField("moveRemaining");
            sonarF.setAccessible(true);
            moveF.setAccessible(true);
            sonarF.set(cp, 0);
            moveF.set(cp, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cp.playOneTurnV2(b, new BoardTextView(b), "Enemy");
        String out = bytes.toString();
        assertTrue(out.contains("missed at") || out.contains("hit your"));
        assertFalse(out.contains("used a special action"));
    }

    @Test
    public void test_move_check_null_coordinate_coverage() {
        PlayerAction action = new PlayerAction();
        Board<Character> b = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory f = new V2ShipFactory();
        Ship<Character> oldS = f.makeBattleship(new Placement("A0U"));
        b.tryAddShip(oldS);
        HashMap<String, Function<Placement, Ship<Character>>> m = new HashMap<>();
        m.put("Battleship", (p) -> f.makeBattleship(p));
        Coordinate h1 = oldS.getCoordinates().iterator().next();
        action.doMove(b, m, h1, new Placement("L6R"));

        Ship<Character> s = b.getShipAt(new Coordinate("Z9"));
        if (s == null) {
            s = b.getShipAt(new Coordinate("L6"));
        }
        assertNotNull(s);
    }

    @Test
    public void test_playOneTurnV2_reflection_exception_coverage() throws IOException {
        V2ShipFactory f = new V2ShipFactory();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');
        ComputerPlayer cp = new ComputerPlayer(b, System.out, "A", f, 10, 20) {
            @Override
            protected boolean shoudUseSpecialAction() {
                return true;
            }
        };
        try {
            Field boardF = ComputerPlayer.class.getDeclaredField("theBoard");
            boardF.setAccessible(true);
            boardF.set(cp, null);
        } catch (Exception e) {
            fail("Setup failed");
        }

        assertThrows(NullPointerException.class, () -> {
            cp.playOneTurnV2(b, new BoardTextView(b), "Enemy");
        });
    }
}