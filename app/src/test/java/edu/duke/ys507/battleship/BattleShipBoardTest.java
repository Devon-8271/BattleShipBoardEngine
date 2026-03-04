package edu.duke.ys507.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


// import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.BomInput.BytesProcessedNotification;

public class BattleShipBoardTest {
    @Test
    public void test_width_and_height() {
        Board<Character> b1 = new BattleShipBoard<Character>(10, 20, 'X');
        assertEquals(10, b1.getWidth());
        assertEquals(20, b1.getHeight());
    }

    @Test
    public void test_invalid_dimensions() {
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(10, 0, 'X'));
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(0, 20, 'X'));
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(10, -5, 'X'));
        assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(-8, 20, 'X'));
    }

    private <T> void checkWhatIsAtBoard(BattleShipBoard<T> b, Character[][] expect) {
        assertEquals(expect.length, b.getHeight());
        assertEquals(expect[0].length, b.getWidth());
        for (int i = 0; i < b.getHeight(); ++i) {
            for (int j = 0; j < b.getWidth(); ++j) {
                assertEquals(expect[i][j], b.whatIsAtForSelf(new Coordinate(i, j)),
                        "Mismatch at " + i + "," + j + "\n");
            }
        }
    }

    private BattleShipBoard<Character> makeBoardandAddShips(int w, int h, int[][] coordinates) {
        BattleShipBoard<Character> b = new BattleShipBoard<>(h, w, 'X');
        for (int[] c : coordinates) {
            b.tryAddShip(new RectangleShip<Character>("submarine", new Coordinate(c[0], c[1]), 1, 1, 's', '*'));
        }
        return b;
    }

    private Character[][] expectedWithShips(int w, int h, int[][] coords) {
        Character[][] expected = new Character[w][h];
        for (int[] c : coords) {
            expected[c[0]][c[1]] = 's';
        }
        return expected;
    }

    @Test
    public void test_board_empty() {
        BattleShipBoard<Character> b = makeBoardandAddShips(10, 5, new int[][]{});
        Character[][] expected = expectedWithShips(10, 5, new int[][]{});
        checkWhatIsAtBoard(b, expected);
    }

    @Test
    public void test_add_ships() {
        BattleShipBoard<Character> b1 = makeBoardandAddShips(3, 3, new int[][] {
                { 1, 2 }, { 0, 1 }
        });

        Character[][] expected = expectedWithShips(3, 3, new int[][] { { 1, 2 }, { 0, 1 } });
        checkWhatIsAtBoard(b1, expected);
    }

    @Test
    public void test_add_ships_checker(){
        assertThrows(IllegalArgumentException.class, ()-> new BattleShipBoard<>(-2, 5,new InBoundsRuleChecker<>(null), 'X'));
        assertThrows(IllegalArgumentException.class, ()->new BattleShipBoard<>(2, -1, new InBoundsRuleChecker<>(null), 'X'));
        BattleShipBoard<Character> b = new BattleShipBoard<>(2, 5, 'X');
        V1ShipFactory f = new V1ShipFactory();
        Ship<Character> s1 = f.makeSubmarine(new Placement("A0h"));
        assertEquals(null, b.tryAddShip(s1));
        Ship<Character> s2 = f.makeSubmarine(new Placement("A4v"));
        assertEquals("That placement is invalid: the ship goes off the right of the board.", b.tryAddShip(s2));
        Ship<Character> s3 = f.makeCarrier(new Placement("A1v"));
        assertEquals("That placement is invalid: the ship goes off the bottom of the board.", b.tryAddShip(s3));
        BattleShipBoard<Character> b1 = new BattleShipBoard<>(2, 5, new NoCollisionRuleChecker<>(new InBoundsRuleChecker<>(null)), 'X');
        b1.tryAddShip(s1);
        assertEquals("That placement is invalid: the ship overlaps another ship.", b1.tryAddShip(s3));
    }   

    @Test
    public void test_fire_at(){
        BattleShipBoard<Character> b = new BattleShipBoard<>(5, 5, 'X');
        V1ShipFactory f = new V1ShipFactory();
        Ship<Character> s1 = f.makeSubmarine(new Placement("A0h"));
        b.tryAddShip(s1);
        assertSame(s1, b.fireAt(new Coordinate("A1")));
        assertEquals(null, b.fireAt(new Coordinate("d2")));
        assertFalse(s1.isSunk());
        b.fireAt(new Coordinate("A0"));
        assertTrue(s1.isSunk());
    }

    @Test 
    public void test_whatisat_for_enemy(){
        BattleShipBoard<Character> b = new BattleShipBoard<Character>(5,5, 'X');
        V1ShipFactory f = new V1ShipFactory();
        Ship<Character> s1 = f.makeSubmarine(new Placement("A0h"));
        b.tryAddShip(s1);
        b.fireAt(new Coordinate("C1"));
        assertEquals('X', b.whatIsAtForEnemy(new Coordinate("C1")));
        assertEquals(null, b.whatIsAt(new Coordinate("D0"), true));
    }

    @Test
    public void test_check_for_win(){
        BattleShipBoard<Character> myb = new BattleShipBoard<Character>(5, 5, 'X');
        BattleShipBoard<Character> enb = new BattleShipBoard<Character>(5, 5, 'X');
        V1ShipFactory factory = new V1ShipFactory();
        Ship<Character> s1 = factory.makeSubmarine(new Placement("A0h"));
        Ship<Character> s2 = factory.makeBattleship(new Placement("A3v"));
        Ship<Character> s3 = factory.makeSubmarine(new Placement("D0v"));
        myb.tryAddShip(s1);
        enb.tryAddShip(s3);
        myb.fireAt(new Coordinate("A1"));
        assertFalse(myb.checkForLose());
        assertFalse(enb.checkForLose());
        myb.fireAt(new Coordinate("A0"));
        assertTrue(enb.checkForWin(myb));
        myb.tryAddShip(s2);
        assertFalse(myb.checkForLose());
        assertFalse(myb.checkForWin(enb));
        

    }

}
