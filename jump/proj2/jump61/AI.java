
package jump61;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        int minValue = Integer.MIN_VALUE;
        int maxValue = Integer.MAX_VALUE;
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            value = minMax(work, 4, true, 1, minValue, maxValue);
        } else {
            value = minMax(work, 4, true, -1, minValue, maxValue);
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (depth == 0 || board.getWinner() != null) {
            return staticEval(board, Integer.MAX_VALUE / 2, sense);
        }
        int bestSoFar = 0;

        if (sense == 1) {
            Side player = RED;
            bestSoFar = Integer.MIN_VALUE;
            List<Integer> legalMoves = getLegalMoves(board, player);

            for (int sqNum : legalMoves) {
                board.addSpot(player, sqNum);
                int response = minMax(board, depth - 1, false, -1, alpha, beta);
                board.undo();
                if (response > bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _foundMove = sqNum;
                    }
                    alpha = max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        if (sense == -1) {
            Side player = BLUE;
            bestSoFar = Integer.MAX_VALUE;
            List<Integer> legalMoves = getLegalMoves(board, player);
            for (int sqNum : legalMoves) {
                board.addSpot(player, sqNum);
                int response = minMax(board, depth - 1, false, 1, alpha, beta);
                board.undo();
                if (response < bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _foundMove = sqNum;
                    }
                    beta = min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        return bestSoFar;
    }
    /**
     * This function gets the legal moves.
     * @return an ArrayList of legal Moves
     * @param board passes in board
     * @param player passes in player
     */
    private List<Integer> getLegalMoves(Board board, Side player) {
        List<Integer> legalMoves = new ArrayList<>();
        for (int row = 1; row < board.size() + 1; row++) {
            for (int col = 1; col < board.size() + 1; col++) {
                if (board.isLegal(player, row, col)) {
                    legalMoves.add(board.sqNum(row, col));
                }
            }
        }
        return legalMoves;
    }
    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue.
     * @param sense passes in sense
     * @param b passes in b
     * @param winningValue passes in sense
     */
    private int staticEval(Board b, int winningValue, int sense) {
        int heuristic;
        Side player;
        if (sense == 1) {
            player = RED;
        } else {
            player = BLUE;
        }
        if (b.getWinner() != null) {
            switch (b.getWinner()) {
            case RED:
                heuristic = winningValue;
                break;
            case BLUE:
                heuristic = -winningValue;
                break;
            default:
                heuristic = b.numOfSide(RED);
                break;
            }
        } else {
            heuristic = b.numOfSide(RED);
        }

        return heuristic;
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}
