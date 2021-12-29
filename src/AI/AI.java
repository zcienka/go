//package AI;
//
//import GameEngine.GameEngine;
//import Pair.Pair;
//
//import java.util.*;
//
//import static AI.Constants.TREE_DEPTH;
//
//// https://medium.com/@ishaan.gupta0401/monte-carlo-tree-search-application-on-chess-5573fc0efb75
//public class AI {
//    //    private Hashtable<Integer, Pair> returnedMove;
//
//    // returns best move, and best move value
//    // ArrayList<Integer> values
////    public ArrayList<Integer> minMax(int depth,
//    public Hashtable<Integer, Pair> minMax(int depth,
//            GameEngine gameEngine,
//            int playerColor,
////            Pair node,
//            int alpha,
//            int beta,
//            ArrayList<Pair> possibleMoves,
//            boolean maximizingPlayer) {
//
//        if (depth == 0) {
////            return new ArrayList<>() {{
////                add(gameEngine.calculatePoints());
////                add(null);
////            }};
////            return gameEngine.calculatePoints();
//
//            return new Hashtable<>(
//                    Map.ofEntries(Map.entry(gameEngine.calculatePlayerOpponentPointRatio(playerColor), new Pair(-1, -1))));
//        }
//        Pair bestMove = possibleMoves.get(0);
////        Collections.shuffle(possibleMoves);
//
//        int bestMoveValue = maximizingPlayer ? -9999 : 9999;
//
//        for (int i = 0; i < possibleMoves.size(); i++) {
//            Pair move = possibleMoves.get(i);
//
//            gameEngine.move(move);
////            gameEngine.addMoveToPastMoves(move);
//
//            Hashtable<Integer, Pair> returnedMove =
//                    minMax(depth - 1, gameEngine, playerColor, alpha, beta, possibleMoves, !maximizingPlayer);
//            Integer value = returnedMove.entrySet().iterator().next().getKey();
//
//            if (maximizingPlayer) {
//                if (value > bestMoveValue) {
//                    bestMoveValue = value;
//                    bestMove = move;
//                }
//                alpha = Math.max(alpha, value);
//            } else {
//                if (value < bestMoveValue) {
//                    bestMoveValue = value;
//                    bestMove = move;
//                }
//                beta = Math.min(beta, value);
//            }
//            gameEngine.undoMove(move);
////            gameEngine.popLastMoveFromPastMoves();
//            if (beta <= alpha) {
////                System.out.println("prune " + alpha + " " + beta);
//                break;
//            }
//        }
//        return new Hashtable<>(Map.ofEntries(Map.entry(bestMoveValue, bestMove)));
//    }
//
//    public Integer findBestMove(GameEngine game) {
//        ArrayList<Pair> possibleMoves = game.findAllPossibleMoves();
//        ArrayList<Integer> moves;
//        Pair bestMove = new Pair(0, 0);
//        int moveValue, bestMoveValue = -9999;
//
////        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
////            for (int j = 0; j < NUMBER_OF_ROWS; j++) {
////        for(Pair move : possibleMoves){
////                if (game.isMoveValid(move.first, move.second)) {
////                    ArrayList<Pair> possibleMoves = game.findAllPossibleMoves();
//        Hashtable<Integer, Pair> returnedMove = minMax(TREE_DEPTH, game, 0, -9999, 9999, possibleMoves, true);
//
////        if (moveValue > bestMoveValue) {
////            bestMoveValue = moveValue;
////            bestMove = new Pair(i, j);
////        }
////                }
////            }
////        }
//        return returnedMove.entrySet().iterator().next().getKey();
////        for (Pair move : possibleMoves) {
////            moves.add(minMax(move));
////        }
//    }
//
////    public double ucbl(Node node) {
////        if (node.n != 0) {
////            return node.v + 2 * (Math.sqrt(Math.log(node.N)) / (node.n));
////        } else {
////            return 0;
////        }
////    }
////
////    public Node selection(Node currNode) {
////        double maxUcb = -9999;
////        Node selectedChild = null;
////        double currUcb;
////
////        for (Node i : currNode.children) {
////            currUcb = ucbl(i);
////            if (currUcb > maxUcb) {
////                maxUcb = currUcb;
////                selectedChild = i;
////            }
////        }
////        return selectedChild;
////    }
////
////    public Node expansion(Node currNode) {
////        if (currNode.children.isEmpty()) {
////            return currNode;
////        }
////        double maxUcb = -9999;
////        Node selectedChild = null;
////        double currUcb;
////
////        for (Node i : currNode.children) {
////            currUcb = ucbl(i);
////            if (currUcb > maxUcb) {
////                maxUcb = currUcb;
////                selectedChild = i;
////            }
////        }
////        return expansion(selectedChild);
////    }
////
////    public double rollout(Node currNode) {
////        if (currNode.state.checkIfNotSuicide()) {
////            if (won) {
////                return 1;
////            } else if (lose) {
////                return -1;
////            } else {
////                return 0.5;
////            }
////        }
////        currNode.children = findAdjacentStones(currNode);
////        Random rand = new Random();
////        ArrayList<Node> array = new ArrayList<>(currNode.children);
////        Node randomChild = array.get(rand.nextInt(array.size()));
////        return rollout(randomChild);
////    }
////
////    public Node findAdjacentStones() {
////        return ;
////    }
////
////    public Node backpropagation(Node currentNode, Integer reward) {
////        while (currentNode.parent != null) {
////            currentNode.v += reward;
////            currentNode = currentNode.parent;
////        }
////        return currentNode;
////    }
//}
