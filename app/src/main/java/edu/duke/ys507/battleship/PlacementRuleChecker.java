package edu.duke.ys507.battleship;

/**
 * An abstract rule checker for validating whether a ship can be placed on a
 * board.
 *
 * <p>
 * Rule checkers are designed to be chained together. Each checker validates one
 * rule
 * in {@code checkMyRule}. If the rule passes, the request is forwarded to the
 * next
 * checker in the chain. If any rule fails, placement is rejected immediately.
 * </p>
 *
 * <p>
 * Subclasses should implement {@code checkMyRule} to define a single placement
 * rule,
 * such as staying within the board boundaries or not overlapping existing
 * ships.
 * </p>
 *
 * @param <T> the board cell type
 */

public abstract class PlacementRuleChecker<T> {
    private final PlacementRuleChecker<T> next;

    /**
     * Constructs a PlacementRuleChecker.
     * 
     * @param next is the next field for this PlacementRuleChecker.
     */
    public PlacementRuleChecker(PlacementRuleChecker<T> next) {
        this.next = next;
    }

    /**
     * Subclasses will override this method to specify how they check their own
     * rule.
     * 
     * @param theShip  is the Ship to be check.
     * @param theBoard is the Board that the ship is gonna be placed.
     * @return null if all the rules are passed, else return the wrong information.
     */
    protected abstract String checkMyRule(Ship<T> theShip, Board<T> theBoard);

    /**
     * Checks if a ship placement passes all rules in this rule checker chain.
     * 
     * <p>
     * This method first checks this rule by calling {@code checkMyRule}.
     * If it returns a non-null String, that String is returned as the error
     * message.
     * Otherwise, it continues to check the next rule checker in the chain.
     * </p>
     * 
     * @param theShip  is the Ship to be checked.
     * @param theBoard is the Board that the ship is gonna be placed.
     * @return null if all rules are passed, else return the wrong information.
     */
    public String checkPlacement(Ship<T> theShip, Board<T> theBoard) {
        // if we fail our own rule: stop the placement is not legal
        String result = checkMyRule(theShip, theBoard);
        if (result != null) {
            return result;
        }
        // other wise, ask the rest of the chain.
        if (next != null) {
            return next.checkPlacement(theShip, theBoard);
        }
        // if there are no more rules, then the placement is legal
        return null;
    }
}
