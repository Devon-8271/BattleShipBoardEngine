package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.Buffer;
import java.util.function.Function;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

public class TextPlayerTest {
    private TextPlayer createTextPlayer(BattleShipBoard<Character> b, String inputData, OutputStream bytes) {
        BufferedReader input = new BufferedReader(new StringReader(inputData));
        PrintStream output = new PrintStream(bytes, true);
        V1ShipFactory shipFactory = new V1ShipFactory();
        return new TextPlayer(b, input, output, "A", shipFactory);
    }

    private void assertOnePlacementOutput(String shipName,
            Function<Placement, Ship<Character>> createFn,
            String inputLine) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(6, 3, 'X');
        TextPlayer player = createTextPlayer(b, inputLine, bytes);

        player.doOnePlacement(shipName, createFn);

        BoardTextView view = new BoardTextView(b);
        String prompt = "Player A where do you want to place a " + shipName + "?\n";

        String expected = prompt + view.displayMyOwnBoard();
        assertEquals(expected, bytes.toString());
    }

    @Test
    public void test_read_placement() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(10, 20, 'X');
        TextPlayer player = createTextPlayer(b, "B2V\nC8H\na4v\n", bytes);

        String prompt = "Please enter a location for a ship:";
        Placement[] expected = new Placement[3];
        expected[0] = new Placement(new Coordinate(1, 2), 'V');
        expected[1] = new Placement(new Coordinate(2, 8), 'H');
        expected[2] = new Placement(new Coordinate(0, 4), 'V');

        for (int i = 0; i < expected.length; i++) {
            Placement p = player.readPlacement(prompt);
            assertEquals(expected[i], p);
            assertEquals(prompt + "\n", bytes.toString());
            bytes.reset();
        }
    }

    @Test
    public void test_do_one_placement_submarine() throws IOException {
        V1ShipFactory f = new V1ShipFactory();
        assertOnePlacementOutput("Submarine", (p) -> f.makeSubmarine(p), "B1H\n");
    }

    @Test
    public void test_do_one_placement_destroyer() throws IOException {
        V1ShipFactory f = new V1ShipFactory();
        assertOnePlacementOutput("Destroyer", (p) -> f.makeDestroyer(p), "B1H\n");
    }

    @Test
    public void test_do_one_placement_battleship() throws IOException {
        V1ShipFactory f = new V1ShipFactory();
        assertOnePlacementOutput("Battleship", (p) -> f.makeBattleship(p), "B1H\n");
    }

    @Test
    public void test_do_one_placement_carrier() throws IOException {
        V1ShipFactory f = new V1ShipFactory();
        assertOnePlacementOutput("Carrier", (p) -> f.makeCarrier(p), "A0H\n");
    }

    @Test
    public void test_do_placement_phase() throws IOException {
        TestTextPlayer p = new TestTextPlayer(
                new BattleShipBoard<>(10, 26, 'X'),
                new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                new V1ShipFactory());

        p.doPlacementPhase();

        assertEquals(10, p.getCalls());
    }

    @Test
    public void test_doOnePlacement_invalid_format() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        TextPlayer player = createTextPlayer(b, "AAV\nB2H\n", bytes);

        player.doOnePlacement("Destroyer", (p) -> player.shipFactory.makeDestroyer(p));

        BoardTextView view = new BoardTextView(b);

        String expected = "Player A where do you want to place a Destroyer?\n" +
                "That placement is invalid: it does not have the correct format.\n" +
                "Player A where do you want to place a Destroyer?\n" +
                view.displayMyOwnBoard();

        assertEquals(expected, bytes.toString());
    }

    @Test
    public void test_doOnePlacement_invalid_orientation() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        TextPlayer player = createTextPlayer(b, "A0Q\nB2H\n", bytes);

        player.doOnePlacement("Destroyer", (p) -> player.shipFactory.makeDestroyer(p));

        String actual = bytes.toString();
        assertTrue(actual.startsWith("Player A where do you want to place a Destroyer?\n"));

        assertTrue(actual.contains("\nPlayer A where do you want to place a Destroyer?\n"));

        BoardTextView view = new BoardTextView(b);
        assertTrue(actual.endsWith(view.displayMyOwnBoard()));
    }

    @Test
    public void test_doOnePlacement_rule_fail_then_ok() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        TextPlayer player = createTextPlayer(b, "E4H\nA0b\nB2H\n", bytes);

        player.doOnePlacement("Destroyer", (p) -> player.shipFactory.makeDestroyer(p));

        BoardTextView view = new BoardTextView(b);

        String expected = "Player A where do you want to place a Destroyer?\n" +
                "That placement is invalid: the ship goes off the right of the board.\n" +
                "Player A where do you want to place a Destroyer?\n" +
                "That placement is invalid: it does not have the correct format.\n" +
                "Player A where do you want to place a Destroyer?\n" +
                view.displayMyOwnBoard();

        assertEquals(expected, bytes.toString());
    }

    @Test
    public void test_doOnePlacement_eof_throws() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        TextPlayer player = createTextPlayer(b, "", bytes);

        assertThrows(EOFException.class, () -> {
            player.doOnePlacement("Destroyer", (p) -> player.shipFactory.makeDestroyer(p));
        });
    }

    @Test
    public void test_doOnePlacement_createFnThrows() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        TextPlayer player = createTextPlayer(b, "B2H\nB2H\n", bytes);

        V1ShipFactory f = new V1ShipFactory();
        final int[] cnt = new int[] { 0 };

        Function<Placement, Ship<Character>> fn = (p) -> {
            if (cnt[0] == 0) {
                cnt[0]++;
                throw new IllegalArgumentException("boom");
            }
            return f.makeDestroyer(p);
        };

        player.doOnePlacement("Destroyer", fn);

        BoardTextView view = new BoardTextView(b);
        String expected = "Player A where do you want to place a Destroyer?\n" +
                "That placement is invalid: it does not have the correct format.\n" +
                "Player A where do you want to place a Destroyer?\n" +
                view.displayMyOwnBoard();

        assertEquals(expected, bytes.toString());
    }

    @Test
    public void test_play_one_turn_coordinate_invalid() {
        BattleShipBoard<Character> myb = new BattleShipBoard<Character>(1, 1, 'X');
        BattleShipBoard<Character> enb = new BattleShipBoard<Character>(1, 1, 'X');
        BoardTextView myv = new BoardTextView(myb);
        BoardTextView env = new BoardTextView(enb);

        BufferedReader br = new BufferedReader(new StringReader("A1v\nA0\n"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        V1ShipFactory f = new V1ShipFactory();
        TextPlayer p1 = new TextPlayer(myb, br, out, "A", f);

        assertDoesNotThrow(() -> p1.playOneTurn(enb, env, "B"));
        assertTrue(bytes.toString().contains("That coordinate is invalid"));
    }

    @Test
    public void test_doSonar_empty_board_center() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        TextPlayer p = new TextPlayer(b, new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()), "A",
                new V1ShipFactory());

        String ans = p.doSonar(new Coordinate(5, 5));
        assertEquals(
                "Submarines occupy 0 squares\n" +
                        "Destroyers occupy 0 squares\n" +
                        "Battleships occupy 0 squares\n" +
                        "Carriers occupy 0 squares\n",
                ans);
    }

    @Test
    public void test_textPlayer_doSonar_counts__carrier() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();

        assertNull(b.tryAddShip(f.makeCarrier(new Placement("B7U"))));

        TextPlayer p = new TextPlayer(
                b,
                new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                new V2ShipFactory());

        String ans = p.doSonar(new Coordinate(5, 5));
        assertEquals(
                "Submarines occupy 0 squares\n" +
                        "Destroyers occupy 0 squares\n" +
                        "Battleships occupy 0 squares\n" +
                        "Carriers occupy 2 squares\n",
                ans);
    }

    @Test
    public void test_doSonar_counts_submarine_inside() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();
        assertNull(b.tryAddShip(f.makeSubmarine(new Placement("F5H"))));

        TextPlayer p = new TextPlayer(b, new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()), "A",
                new V1ShipFactory());

        String ans = p.doSonar(new Coordinate(5, 5));
        assertEquals(
                "Submarines occupy 2 squares\n" +
                        "Destroyers occupy 0 squares\n" +
                        "Battleships occupy 0 squares\n" +
                        "Carriers occupy 0 squares\n",
                ans);
    }

    @Test
    public void test_doSonar_counts_multiple_and_offboard_center() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();

        assertNull(b.tryAddShip(f.makeDestroyer(new Placement("A0H"))));
        assertNull(b.tryAddShip(f.makeBattleship(new Placement("B2R"))));
        assertNull(b.tryAddShip(f.makeCarrier(new Placement("E6U"))));

        TextPlayer p = new TextPlayer(b, new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()), "A",
                new V1ShipFactory());

        String ans = p.doSonar(new Coordinate(0, 0));
        assertEquals(
                "Submarines occupy 0 squares\n" +
                        "Destroyers occupy 3 squares\n" +
                        "Battleships occupy 1 squares\n" +
                        "Carriers occupy 0 squares\n",
                ans);
    }

    @Test
    public void test_textPlayer_doMove_delegates_and_preserves_damage() {
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(10, 10, 'X');
        V2ShipFactory f = new V2ShipFactory();

        assertNull(b.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        b.fireAt(new Coordinate(1, 1));

        TextPlayer p = new TextPlayer(
                b,
                new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                new V2ShipFactory());

        String err = p.doMove(new Coordinate(1, 1), new Placement("D4U"));
        assertNull(err);

        assertNull(b.getShipAt(new Coordinate(1, 1)));

        Ship<Character> moved = b.getShipAt(new Coordinate(4, 5));
        assertNotNull(moved);

        assertEquals(Character.valueOf('*'), b.whatIsAtForSelf(new Coordinate(4, 5)));
        assertEquals(Character.valueOf('b'), b.whatIsAtForEnemy(new Coordinate(4, 5)));
    }

    @Test
    public void test_playOneTurnV2_fire_hit() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<Character>(10, 10, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');

        V2ShipFactory f = new V2ShipFactory();
        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        String input = "F\nA0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextPlayer p = new TextPlayer(my, new BufferedReader(new StringReader(input)), new PrintStream(bytes), "A", f);

        BoardTextView enemyView = new BoardTextView(enemy);
        p.playOneTurnV2(enemy, enemyView, "B");

        String out = bytes.toString();
        assertTrue(out.contains("You hit a"));
    }

    @Test
    public void test_playOneTurnV2_move_success_decrements_and_moves_ship() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<Character>(10, 10, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');

        V2ShipFactory f = new V2ShipFactory();
        assertNull(my.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        my.fireAt(new Coordinate(1, 1));

        String input = "M\nB1\nD4U\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextPlayer p = new TextPlayer(my, new BufferedReader(new StringReader(input)), new PrintStream(bytes), "A", f);

        int before = p.getMoveRemaining();

        BoardTextView enemyView = new BoardTextView(enemy);
        p.playOneTurnV2(enemy, enemyView, "B");

        int after = p.getMoveRemaining();
        assertEquals(before - 1, after);

        assertNull(my.getShipAt(new Coordinate(1, 1)));
        assertNotNull(my.getShipAt(new Coordinate(4, 5)));
        assertEquals(Character.valueOf('*'), my.whatIsAtForSelf(new Coordinate(4, 5)));
    }

    @Test
    public void test_playOneTurnV2_move_fail_does_not_decrement_then_fire() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<Character>(10, 10, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');

        V2ShipFactory f = new V2ShipFactory();
        assertNull(my.tryAddShip(f.makeBattleship(new Placement("A0U"))));

        String input = "M\nJ9\nD4U\nF\nA0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextPlayer p = new TextPlayer(my, new BufferedReader(new StringReader(input)), new PrintStream(bytes), "A", f);

        int before = p.getMoveRemaining();

        BoardTextView enemyView = new BoardTextView(enemy);
        p.playOneTurnV2(enemy, enemyView, "B");

        int after = p.getMoveRemaining();
        assertEquals(before, after);

        String out = bytes.toString();
        assertTrue(out.contains("There is no ship"));
    }

    @Test
    public void test_playOneTurnV2_sonar_three_times_then_disallowed_and_fire() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<Character>(10, 10, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');

        V2ShipFactory f = new V2ShipFactory();
        assertNull(my.tryAddShip(f.makeSubmarine(new Placement("F5H"))));
        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        StringBuilder sb = new StringBuilder();
        sb.append("S\nF5\n");
        sb.append("S\nF5\n");
        sb.append("S\nF5\n");
        sb.append("S\nF\nA0\n");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextPlayer p = new TextPlayer(my, new BufferedReader(new StringReader(sb.toString())), new PrintStream(bytes),
                "A",
                f);

        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        assertEquals(0, p.getSonarRemaining());

        String out = bytes.toString();
        assertTrue(out.contains("No sonar scans remaining."));
        assertTrue(out.contains("You hit a"));
    }

    @Test
    public void test_playOneTurnV2_both_special_exhausted_then_rejects_M_and_S_and_fire() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<Character>(10, 10, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');

        V2ShipFactory f = new V2ShipFactory();
        assertNull(my.tryAddShip(f.makeBattleship(new Placement("A0U"))));
        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        StringBuilder sb = new StringBuilder();
        sb.append("S\nF5\n");
        sb.append("S\nF5\n");
        sb.append("S\nF5\n");
        sb.append("M\nB1\nD4U\n");
        sb.append("M\nE5\nG1U\n");
        sb.append("M\nG2\nH5U\n");
        sb.append("M\nS\nF\nA0\n");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextPlayer p = new TextPlayer(my, new BufferedReader(new StringReader(sb.toString())), new PrintStream(bytes),
                "A",
                f);

        for (int i = 0; i < 7; i++) {
            p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        }

        assertEquals(0, p.getMoveRemaining());
        assertEquals(0, p.getSonarRemaining());

        String out = bytes.toString();
        assertTrue(out.contains("No moves remaining."));
        assertTrue(out.contains("No sonar scans remaining."));
        assertTrue(out.contains("You hit a"));
    }

    @Test
    public void test_readCoordinate_EOF() {
        BattleShipBoard<Character> my = new BattleShipBoard<>(5, 5, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(5, 5, 'X');
        V2ShipFactory f = new V2ShipFactory();

        BufferedReader input = new BufferedReader(new StringReader(""));
        TextPlayer p = new TextPlayer(my, input, new PrintStream(new ByteArrayOutputStream()), "A", f);

        assertThrows(EOFException.class, () -> {
            p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        });
    }

    @Test
    public void test_readCoordinate_invalid_then_valid() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<>(5, 5, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(5, 5, 'X');
        V2ShipFactory f = new V2ShipFactory();

        String inputStr = "S\nZ99\nA0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        TextPlayer p = new TextPlayer(
                my,
                new BufferedReader(new StringReader(inputStr)),
                new PrintStream(bytes),
                "A",
                f);

        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String out = bytes.toString();
        assertTrue(out.contains("That coordinate is invalid"));
    }

    @Test
    public void test_readAction_EOF() {
        BattleShipBoard<Character> my = new BattleShipBoard<>(5, 5, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(5, 5, 'X');
        V2ShipFactory f = new V2ShipFactory();

        TextPlayer p = new TextPlayer(
                my,
                new BufferedReader(new StringReader("S\n")),
                new PrintStream(new ByteArrayOutputStream()),
                "A",
                f);

        assertThrows(EOFException.class, () -> {
            p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");
        });
    }

    @Test
    public void test_readAction_emptyLine_then_fire() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<>(5, 5, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(5, 5, 'X');
        V2ShipFactory f = new V2ShipFactory();

        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        String input = "\nF\nA0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        TextPlayer p = new TextPlayer(
                my,
                new BufferedReader(new StringReader(input)),
                new PrintStream(bytes),
                "A",
                f);

        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String out = bytes.toString();
        assertTrue(out.contains("You hit a"));
    }

    @Test
    public void test_readAction_invalid_then_fire() throws IOException {
        BattleShipBoard<Character> my = new BattleShipBoard<>(5, 5, 'X');
        BattleShipBoard<Character> enemy = new BattleShipBoard<>(5, 5, 'X');
        V2ShipFactory f = new V2ShipFactory();

        assertNull(enemy.tryAddShip(f.makeSubmarine(new Placement("A0H"))));

        String input = "Q\nF\nA0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        TextPlayer p = new TextPlayer(
                my,
                new BufferedReader(new StringReader(input)),
                new PrintStream(bytes),
                "A",
                f);

        p.playOneTurnV2(enemy, new BoardTextView(enemy), "B");

        String out = bytes.toString();
        assertTrue(out.contains("Please enter F, M, or S."));
        assertTrue(out.contains("You hit a"));
    }

    @Test
    public void test_invalidMovePlacement_withoutReflection() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        Board<Character> myBoard = new BattleShipBoard<Character>(10, 10, 'X');
        V1ShipFactory factory = new V1ShipFactory();
        myBoard.tryAddShip(factory.makeDestroyer(new Placement("A0H")));

        Board<Character> enemy = new BattleShipBoard<Character>(10, 10, 'X');
        BoardTextView enemyView = new BoardTextView(enemy);

        String input = String.join("\n",
                "M", "A0", "A0Q",
                "F", "A0") + "\n";

        BufferedReader br = new BufferedReader(new StringReader(input));
        TextPlayer p = new TextPlayer(myBoard, br, out, "A", factory);

        p.playOneTurnV2(enemy, enemyView, "B");

        String s = bytes.toString();
        assertTrue(s.contains("That placement is invalid: it does not have the correct format."));
    }

    @Test
    public void test_doPlacementPhaseV2_runs() throws Exception {
        Board<Character> b = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory f = new V2ShipFactory();

        String input = "A0H\nA5H\nA9vn" + // sub
                "B0H\nB5H\nY2h\n" + // sub
                "C0H\nC6H\nC4v\n" + // des
                "D0H\nD6H\nD1v\n" + // des
                "E0H\nE6H\nE9v\n" + // des
                "F0U\nF8U\nF6v\n" + // bat
                "G0U\nG8U\nG3u\n" + // bat
                "H0U\nH8U\nH9l\n" + // bat
                "I0U\nI9U\n" + // car
                "J0U\nJ1U\n"; // car

        BufferedReader in = new BufferedReader(new StringReader(input));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TextPlayer tp = new TextPlayer(b, in, out, "A", f);

        tp.doPlacementPhaseV2();

        assertNotNull(b.getShipAt(new Coordinate(0, 0)));
    }

}