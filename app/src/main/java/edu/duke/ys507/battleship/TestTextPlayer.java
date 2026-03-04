package edu.duke.ys507.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A test-only subclass of TextPlayer used to verify control flow in placement
 * logic.
 *
 * This class overrides doOnePlacementto avoid reading input or modifying the
 * board.
 * Instead, it records how many times doOnePlacement
 * invoked, so tests can confirm that doPlacementPhase
 * attempts to place the expected number of ships without
 * relying on printed output or board state.
 * 
 * @param calls record the times doOnePlacement is run.
 */
public class TestTextPlayer extends TextPlayer {
    private int calls = 0;

    /**
     * Constructs by calling the super class constructor,
     * 
     * @param b    is the board used by this player
     * @param in   is the reader used for input.
     * @param out  is the stream used for output.
     * @param name is the player's name.
     * @param f    is the ship factory used to create ships.
     */
    public TestTextPlayer(Board<Character> b,
            BufferedReader in,
            PrintStream out,
            String name,
            V1ShipFactory f) {
        super(b, in, out, name, f);
    }

    /**
     * Override doOnePlacement to increase number of calls.
     */
    @Override
    public void doOnePlacement(String shipName,
            java.util.function.Function<Placement, Ship<Character>> fn)
            throws IOException {
        calls++;
    }
    /**
     * @return calls.
     */
    public int getCalls() {
        return calls;
    }
}
