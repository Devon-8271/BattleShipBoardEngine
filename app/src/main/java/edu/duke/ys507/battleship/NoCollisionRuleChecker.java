package edu.duke.ys507.battleship;

/**
 * A placement rule checker that verifies a ship does not collide with any other ships on the board.
 * 
 * <p>
 * This rule iterates over all coordinates occupied by the ship and ensures each one is not alrealy occupied by any other ship.
 * </p>
 * 
 * @param <T> the board cell type
 */
public class NoCollisionRuleChecker<T> extends PlacementRuleChecker<T>{
    /**
     * A constructor that takes a PlacementRuleChecker<T> and passes it to the 
     * super class's constructor.
     * @param next it the PlacementRuleChecker<T>.
     */
    public NoCollisionRuleChecker(PlacementRuleChecker<T> next){
        super(next);
    }

    /**
     * Checks if the coordinates of the ship is already occupied by other ships.
     * @param theShip is the ship to be checked.
     * @param theBoard is the board to be placed.
     * @return null if each coordinate in the ship is not occupied on the board, else the wrong information.
     */
    @Override
    protected String checkMyRule(Ship<T> theShip, Board<T> theBoard){
        for (Coordinate c: theShip.getCoordinates()){
            if (theBoard.whatIsAtForSelf(c) != null){
                return "That placement is invalid: the ship overlaps another ship.";
            }
        }
        return null;
    }
}
