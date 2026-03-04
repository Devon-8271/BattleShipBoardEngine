package edu.duke.ys507.battleship;

/**
 * A placement rule checker that verifies a ship is fully within the board boundaries.
 *
 * <p>
 * This rule iterates over all coordinates occupied by the ship and ensures each one
 * lies within the valid row/column range of the board. If any coordinate is outside
 * the board, the placement is rejected.
 * </p>
 *
 * @param <T> the board cell type
 */

public class InBoundsRuleChecker<T> extends PlacementRuleChecker<T> {
    /**
     * A constructor that takes a PlacementRuleChecker<T> and passes it to the 
     * super class's constructor.
     * @param next it the PlacementRuleChecker<T>.
     */
    public InBoundsRuleChecker(PlacementRuleChecker<T> next) {
        super(next);
    }

    /**
     * Checks if the ship's coordinates are in the bound of the board.
     * @param theShip is the ship to be checked.
     * @param theBoard is the board to be placed.
     * @return null if the ship is in the bound of the board, else the wrong information.
     */
    @Override
    protected String checkMyRule(Ship<T> theShip, Board<T> theBoard) {
        for (Coordinate c : theShip.getCoordinates()) {
            if (c.getRow() < 0){
                return "That placement is invalid: the ship goes off the top of the board.";
            }
            else if (c.getRow() >= theBoard.getHeight()){
                return "That placement is invalid: the ship goes off the bottom of the board.";
            }
            else if (c.getColumn() < 0){
                return "That placement is invalid: the ship goes off the left of the board.";
            }
            else if (c.getColumn() >= theBoard.getWidth()){
                return "That placement is invalid: the ship goes off the right of the board.";
            }
        }
        return null;
    }

}
