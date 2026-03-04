package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BoardTextViewTest {
    private void emptyBoardHelper(int w, int h, String expectedHeader, String expectedBody) {
        Board<Character> b1 = new BattleShipBoard<>(w, h, 'X');
        BoardTextView view = new BoardTextView(b1);
        assertEquals(expectedHeader, view.makeHeader());
        String expected = expectedHeader + expectedBody + expectedHeader;
        assertEquals(expected, view.displayMyOwnBoard());
    }

    private void boardHelper(BattleShipBoard<Character> b, String expectedHeader, String expectedBody) {
        BoardTextView view = new BoardTextView(b);
        assertEquals(expectedHeader, view.makeHeader());
        String expected = expectedHeader + expectedBody + expectedHeader;
        assertEquals(expected, view.displayMyOwnBoard());
    }

    @Test
    public void test_display_empty_2by2() {
        String expectedHeader = "  0|1\n";
        String expectedBody = "A  |  A\n" +
                "B  |  B\n";
        emptyBoardHelper(2, 2, expectedHeader, expectedBody);
    }

    @Test
    public void test_display_empty_3by2() {
        String expectedHeader = "  0|1\n";
        String expectedBody = "A  |  A\n" +
                "B  |  B\n" +
                "C  |  C\n";
        emptyBoardHelper(2, 3, expectedHeader, expectedBody);
    }

    @Test
    public void test_display_empty_3by5() {
        String expectedHeader = "  0|1|2|3|4\n";
        String expectedBody = "A  | | | |  A\n" +
                "B  | | | |  B\n" +
                "C  | | | |  C\n";
        emptyBoardHelper(5, 3, expectedHeader, expectedBody);
    }

    @Test
    public void test_invalid_board_size() {
        Board<Character> wideBoard = new BattleShipBoard<Character>(11, 20, 'X');
        Board<Character> tallBoard = new BattleShipBoard<Character>(10, 27, 'X');
        assertThrows(IllegalArgumentException.class, () -> new BoardTextView(wideBoard));
        assertThrows(IllegalArgumentException.class, () -> new BoardTextView(tallBoard));
    }

    @Test
    public void test_board_add_ship() {
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 3, 'X');
        String expectedHeader = "  0|1|2|3|4\n";
        String expectedBody = "A  | | | |  A\n" +
                "B  | | | |  B\n" +
                "C  | | | |  C\n";
        boardHelper(b, expectedHeader, expectedBody);
        b.tryAddShip(new RectangleShip<Character>(new Coordinate(1, 3), 's', '*'));
        String expectedBody1 = "A  | | | |  A\n" +
                "B  | | |s|  B\n" +
                "C  | | | |  C\n";
        boardHelper(b, expectedHeader, expectedBody1);
        b.tryAddShip(new RectangleShip<Character>(new Coordinate(0, 7), 's', '*'));
        boardHelper(b, expectedHeader, expectedBody1);
        b.tryAddShip(new RectangleShip<Character>(new Coordinate(0, 1), 's', '*'));
        String expectedBody2 = "A  |s| | |  A\n" +
                "B  | | |s|  B\n" +
                "C  | | | |  C\n";
        boardHelper(b, expectedHeader, expectedBody2);
    }

    @Test
    public void test_display_enemy_board() {
        String myView = "  0|1|2|3\n" +
                "A  | | |d A\n" +
                "B s|s| |d B\n" +
                "C  | | |d C\n" +
                "  0|1|2|3\n";
        BattleShipBoard<Character> b = new BattleShipBoard<>(4, 3, 'X');
        V1ShipFactory v = new V1ShipFactory();
        b.tryAddShip(v.makeDestroyer(new Placement("A3v")));
        b.tryAddShip(v.makeSubmarine(new Placement("B0h")));
        b.fireAt(new Coordinate("A3"));
        b.fireAt(new Coordinate("C1"));
        String expected_header = "  0|1|2|3\n";
        String expected_body = "A  | | |d A\n" +
                "B  | | |  B\n" +
                "C  |X| |  C\n";
        BoardTextView view = new BoardTextView(b);
        assertEquals(view.displayEnemyBoard(), expected_header + expected_body + expected_header);

    }
    @Test
    public void test_display_myboard_with_enemyboard(){
        BattleShipBoard<Character> b1 = new BattleShipBoard<>(10, 20, 'X');
        BattleShipBoard<Character> b2 = new BattleShipBoard<>(10, 20, 'X');
        BoardTextView v1 = new BoardTextView(b1);
        BoardTextView v2 = new BoardTextView(b2);
         b2.fireAt(new Coordinate("A0"));

        String result = v1.displayMyBoardWithEnemyNextToIt(v2, "Your ocean", "Player B's ocean");
        String[] result_line = result.split("\n");
        assertEquals("Your ocean", result_line[0].substring(5, 5 + "Your ocean".length()));
        assertEquals("Player B's ocean", result_line[0].substring(42, 42 + "Player B's ocean".length()));
        assertEquals('X', result_line[2].charAt(41));
    }

}
