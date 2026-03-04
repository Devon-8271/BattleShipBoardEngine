package edu.duke.ys507.battleship;

import java.util.HashSet;
import java.util.Set;

public class TestUtils {
    public static Set<Coordinate> toSet(Iterable<Coordinate> it) {
        Set<Coordinate> ans = new HashSet<>();
        for (Coordinate c : it) {
            ans.add(c);
        }
        return ans;
    }
}

