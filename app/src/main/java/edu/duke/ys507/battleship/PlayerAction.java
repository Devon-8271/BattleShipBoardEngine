package edu.duke.ys507.battleship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PlayerAction {

    /**
     * Sonar scan.
     * Considers the following pattern
     * around (and including) the center (C)
     *
     ***
     *****
     *** 
     * C***
     *****
     ***
     *
     * 
     * and reports on the number of squares occupied by each
     * type of ship in that region.
     * 
     * @param theBoard is the Board worked on.
     * @param center   is the coordinate of the center of sonar scan.
     * @return a String describing the number of each ship.
     */
    public String doSonar(Board<Character> theBoard, Coordinate center) {
        int r0 = center.getRow();
        int c0 = center.getColumn();

        int sub = 0, des = 0, car = 0, bat = 0;
        for (int dr = -3; dr <= 3; ++dr) {
            int span = 3 - Math.abs(dr);
            for (int dc = -span; dc <= span; ++dc) {
                int r = r0 + dr;
                int c = c0 + dc;
                if (r < 0 || r >= theBoard.getHeight() || c < 0 || c >= theBoard.getWidth()) {
                    continue;
                }
                Ship<Character> s = theBoard.getShipAt(new Coordinate(r, c));
                if (s == null) {
                    continue;
                }
                String name = s.getName();
                if (name.equals("Submarine")) {
                    sub++;
                } else if (name.equals("Destroyer")) {
                    des++;
                } else if (name.equals("Battleship")) {
                    bat++;
                } else {
                    car++;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Submarines occupy ").append(sub).append(" squares\n");
        sb.append("Destroyers occupy ").append(des).append(" squares\n");
        sb.append("Battleships occupy ").append(bat).append(" squares\n");
        sb.append("Carriers occupy ").append(car).append(" squares\n");
        return sb.toString();
    }

    /**
     * Find the upperLeft corner of the smallest rectangle which fully
     * encloses the ship.
     * 
     * @param s is the ship.
     * @return the coordinate of the upperleft corner.
     */
    private Coordinate findUpperLeft(Ship<Character> s) {
        int r = Integer.MAX_VALUE;
        int c = Integer.MAX_VALUE;
        for (Coordinate o : s.getCoordinates()) {
            if (o.getColumn() < c) {
                c = o.getColumn();
            }
            if (o.getRow() < r) {
                r = o.getRow();
            }
        }
        return new Coordinate(r, c);
    }

    /**
     * Copy the relative location of the damage from the old ship to the new ship
     * and record that hit.
     * 
     * @param oldShip is the old ship before move.
     * @param newShip is the new ship after move.
     */
    private void copyDamage(Ship<Character> oldShip, Ship<Character> newShip) {
        Coordinate old_upperleft = findUpperLeft(oldShip);
        Coordinate new_upperleft = findUpperLeft(newShip);
        Set<P> oldRel = rel(oldShip, old_upperleft);
        Set<P> newNorm = norm(rel(newShip, new_upperleft));

        int chosen = -1;
        P oldRotMin = null;
        for (int k = 0; k < 4; k++) {
            Set<P> rs = new HashSet<>();
            for (P p : oldRel)
                rs.add(rot(p, k));
            P m = min(rs);

            Set<P> n = new HashSet<>();
            for (P p : rs)
                n.add(new P(p.r - m.r, p.c - m.c));

            if (n.equals(newNorm)) {
                chosen = k;
                oldRotMin = m;
                break;
            }
        }
        if (chosen == -1) {
            throw new IllegalArgumentException("Cannot copy damage: ship geometry mismatch");
        }

        for (Coordinate c : oldShip.getCoordinates()) {
            if (!oldShip.wasHitAt(c))
                continue;

            P p = new P(c.getRow() - old_upperleft.getRow(), c.getColumn() - old_upperleft.getColumn());
            P q = rot(p, chosen);
            P qq = new P(q.r - oldRotMin.r, q.c - oldRotMin.c);

            Coordinate mapped = new Coordinate(new_upperleft.getRow() + qq.r, new_upperleft.getColumn() + qq.c);
            newShip.recordHitAt(mapped);
        }
    }

    /**
     * Move one ship on the board to another place on the board.
     * Get the ship from the given coordinate, create a new ship with the
     * shipcreation function.
     * Try to create new ship and catch the illegalargument exception.
     * Then remove the old ship from the shiplist on the board, and add the new
     * ship.
     * Copy damage information of the old ship to the new ship.
     * 
     * @param theBoard        is the Board worked on.
     * @param shipCreationFns is the function to create ship of different types.
     * @param oldCoordinate   is the coordinate of ship that the user want to move.
     * @param newPlacement    is the final placement after the move.
     * @return error messages if 1) No ship at the old coordinate. 2) No
     *         corresponding ship creation function. 3) Invalid format of new
     *         placement.
     *         4) Fail to remove ship from the board ship list. 5) Cannot add
     *         newship to the board. If there is no such errors, return null.
     */
    public String doMove(Board<Character> theBoard,
            HashMap<String, Function<Placement, Ship<Character>>> shipCreationFns, Coordinate oldCoordinate,
            Placement newPlacement) {
        Ship<Character> oldShip = theBoard.getShipAt(oldCoordinate);
        if (oldShip == null) {
            return "There is no ship at: (" + oldCoordinate.getRow() + ", " + oldCoordinate.getColumn() + ")\n";
        }
        Function<Placement, Ship<Character>> fn = shipCreationFns.get(oldShip.getName());
        if (fn == null) {
            return "Unknown ship type\n";
        }
        Ship<Character> newShip;
        try {
            newShip = fn.apply(newPlacement);
        } catch (IllegalArgumentException e) {
            return "That placement is invalid: it does not have the correct format\n";
        }
        if (!theBoard.removeShip(oldShip)) {
            return "Could not remove ship from the board\n";
        }
        String err = theBoard.tryAddShip(newShip);
        if (err != null) {
            // Add the old ship back to the board if the new one fail.
            theBoard.tryAddShip(oldShip);
            return err;
        }
        copyDamage(oldShip, newShip);
        return null;
    }

    /**
     * Class point contains r and c.
     */
    private static final class P {
        final int r;
        final int c;

        P(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof P))
                return false;
            P p = (P) o;
            return r == p.r && c == p.c;
        }

        @Override
        public int hashCode() {
            return 31 * r + c;
        }
    }

    /**
     * Rotates a relative point around the origin by 0/90/180/270 degrees.
     *
     * @param p is the point to rotate
     * @param k is the rotation index: 0->0°, 1->90°, 2->180°, 3->270°
     * @return the rotated point (may contain negative coordinates)
     */
    private P rot(P p, int k) {
        int x = p.r, y = p.c;
        if (k == 0)
            return new P(x, y);
        if (k == 1)
            return new P(y, -x);
        if (k == 2)
            return new P(-x, -y);
        return new P(-y, x);
    }

    /**
     * Finds the minimum row and minimum column among all points in the set.
     *
     * @param s is a non-empty set of points
     * @return a point (minRow, minCol)
     */
    private P min(Set<P> s) {
        int mr = Integer.MAX_VALUE, mc = Integer.MAX_VALUE;
        for (P p : s) {
            if (p.r < mr)
                mr = p.r;
            if (p.c < mc)
                mc = p.c;
        }
        return new P(mr, mc);
    }

    /**
     * Converts a ship's absolute coordinates into relative coordinates with respect
     * to a given upper-left corner.
     *
     * @param ship is the ship whose coordinates are converted
     * @param ul   is the upper-left reference coordinate
     * @return a set of relative points describing the ship shape
     */
    private Set<P> rel(Ship<Character> ship, Coordinate ul) {
        Set<P> out = new HashSet<>();
        for (Coordinate c : ship.getCoordinates()) {
            out.add(new P(c.getRow() - ul.getRow(), c.getColumn() - ul.getColumn()));
        }
        return out;
    }

    /**
     * Normalizes a set of points by translating it so that its minimum row and
     * minimum column become (0,0).
     *
     * @param s is a non-empty set of points
     * @return a normalized set of points
     */
    private Set<P> norm(Set<P> s) {
        P m = min(s);
        Set<P> out = new HashSet<>();
        for (P p : s)
            out.add(new P(p.r - m.r, p.c - m.c));
        return out;
    }

}
