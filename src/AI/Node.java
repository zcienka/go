package AI;

import GameEngine.GameEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    public GameEngine state;
    //    public ArrayList<List<Integer>> state;
    public Set<Node> children;
    public Node parent;
    public Integer N; // ni = number of times child node has been visited.
    public Integer n; // N = Number of times parent node has been visited
    public Integer v; // V = winning score of current node

    Node(GameEngine g) {
        this.state = g; // game board
        this.children = new HashSet<>();
        this.parent = null;
        this.N = 0;
        this.n = 0;
        this.v = 0;

    }
}

//    public int minMax(Pair node, int depth, boolean maximizingPlayer) {
//        if (depth == 3) {
//            return;
//        }
//
//        if (maximizingPlayer) {
//            int value = -9999;
////            for (int i=0; i<2; i++) {
////                value = minMax(depth + 1, , false, )
////            }
//            for (int i = 0; i < NUMBER_OF_ROWS; i++) {
//                for (int j = 0; j < NUMBER_OF_ROWS; j++) {
//                    if (board[i][j] == -1) {
//                        board[i][j] =;
//                        best = max(best, minMax(board, depth + 1, !isMax));
//                        board[i][j] = '_';
//                    }
//                }
//            }
//        } else {
//
//        }
//    }

//    public int zobristHashing() {
//
//    }
//    public state
