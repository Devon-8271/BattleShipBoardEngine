package edu.duke.ys507.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * /**
 * The top-level application class for the Battleship program.
 *
 * <p>
 * This class wires together the game components (boards, players, input, and
 * output)
 * and coordinates the overall program flow.
 * </p>
 *
 * <p>
 * The constructor accepts two players, which makes the application easier to
 * test
 * by injecting test boards and custom input/output streams instead of relying
 * on
 * System.in and System.out.
 * </p>
 */

public class App {

    private final Player player1;
    private final Player player2;

    /**
     * Constructs an App with given 2 Players.
     * 
     * @param p1 is player1.
     * @param p2 is player2.
     */
    public App(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    /**
     * Program entry point. Creates two players and do one placement phase.
     *
     * @throws IOException if input fails
     */
    public static void main(String[] args) throws IOException {
        Board<Character> b1 = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> b2 = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory factory = new V2ShipFactory();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Player p1 = createPlayer("A", b1, input, System.out, factory, 10, 20);
        Player p2 = createPlayer("B", b2, input, System.out, factory, 10, 20);
        App app = new App(p1, p2);
        app.doPlacementPhaseV2();
        app.doAttackPhaseV2();
    }

    /**
     * Let player 1 do one placement.
     * 
     * @throws IOException if read input failed.
     */
    public void doPlacementPhase() throws IOException {
        player1.doPlacementPhase();
        player2.doPlacementPhase();
    }

    /**
     * Let player 1 do one placement.
     * 
     * @throws IOException if read input failed.
     */
    public void doPlacementPhaseV2() throws IOException {
        player1.doPlacementPhaseV2();
        player2.doPlacementPhaseV2();
    }

    /**
     * Play attack phase.
     * Start from player1 and take turn. First playOneTurn then check if the player
     * win/miss.
     * 
     * @throws IOException if reading input fails.
     */
    public void doAttackPhase() throws IOException {
        while (true) {
            player1.playOneTurn(player2.getBoard(), player2.getView(), "B");
            if (player1.getBoard().checkForWin(player2.getBoard())) {
                player1.announceWin();
                return;
            }
            player2.playOneTurn(player1.getBoard(), player1.getView(), "A");
            if (player2.getBoard().checkForWin(player1.getBoard())) {
                player2.announceWin();
                return;
            }
        }
    }

    /**
     * Play attack phase with version 2.
     * Start from player1 and take turn. First playOneTurn then check if the player
     * win/miss.
     * 
     * @throws IOException if reading input fails.
     */
    public void doAttackPhaseV2() throws IOException {
        while (true) {
            player1.playOneTurnV2(player2.getBoard(), player2.getView(), player2.getName());
            if (player1.getBoard().checkForWin(player2.getBoard())) {
                player1.announceWin();
                return;
            }

            player2.playOneTurnV2(player1.getBoard(), player1.getView(), player1.getName());
            if (player2.getBoard().checkForWin(player1.getBoard())) {
                player2.announceWin();
                return;
            }
        }
    }

    /**
     * Create player depending on the input string to return a textplayer/computerplayer.
     * @param name is the name of the player.
     * @param board is the player's board.
     * @param input is the BufferedReader.
     * @param out is the PrintStream.
     * @param factory is the abstract ship factory.
     * @param width is the width of the board.
     * @param height is the height of the board.
     * @return a ComputerPlayer if the input is c; a TextPlayer if the input is h; otherwise continue and read next line.
     * @throws IOException if there is no input.
     */
    public static Player createPlayer(String name, Board<Character> board, BufferedReader input,
            PrintStream out, AbstractShipFactory<Character> factory,
            int width, int height) throws IOException {
        while (true){
            out.println("Is Player " + name + " a human or computer? (h/c)");
            String line = input.readLine();
            if (line == null){
                throw new IOException("No input\n");
            }
            line = line.trim().toLowerCase();
            if (line.equals("h")){
                return new TextPlayer(board, input, out, name, factory);
            }
            else if (line.equals("c")){
                return new ComputerPlayer(board, out, name, factory, height, width);
            }
            else{
                out.println("Please enter 'h' or 'c'");
            }
        }
    
    }
}
