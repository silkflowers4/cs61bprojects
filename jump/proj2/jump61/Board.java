
package jump61;

import java.util.ArrayDeque;
import java.util.Formatter;

import java.util.Stack;
import java.util.function.Consumer;

import static jump61.Side.*;
import static jump61.Square.square;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered from 0 to size()-1, in
 *  row 2 numbered from size() to 2*size() - 1, etc. (i.e., row-major order).
 *
 *  A Board may be given a notifier---a Consumer<Board> whose
 *  .accept method is called whenever the Board's contents are changed.
 *
 *  @author Joshua Park
 */
class Board {

    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** A read-only version of this Board. */
    private ConstantBoard _readonlyBoard;

    /** Use _notifier.accept(B) to announce changes to this board. */
    private Consumer<Board> _notifier;



    /** 2D Square array to represent the board. */
    private Square[][] _squares;

    /** Total number of squares on a side. */
    private int _numSquaresOnSide;

    /** Stores the previous boards. */
    private Stack<Square[][]> _undoHistory;

    /** An uninitialized Board.  Only for use by subtypes. */
    protected Board() {
        _notifier = NOP;
    }

    /** An N x N board in initial configuration. */
    Board(int N) {
        this();
        this._squares = new Square[N + 1][N + 1];
        this._undoHistory = new Stack<>();
        this._numSquaresOnSide = N;

        for (int row = 1; row < N + 1; row++) {
            for (int col = 1; col < N + 1; col++) {
                _squares[row][col] = Square.INITIAL;
            }
        }
        _readonlyBoard = new ConstantBoard(this);
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear, and whose notifier does nothing. */
    Board(Board board0) {
        this(board0.size());
        int length = board0.size();
        this._squares = new Square[length + 1][length + 1];

        for (int row = 1; row < length + 1; row++) {
            for (int col = 1; col < length + 1; col++) {
                int spots = board0.get(row, col).getSpots();
                Side side = board0.get(row, col).getSide();
                internalSet(row, col, spots, side);
            }
        }
        this._undoHistory = new Stack<>();
        _readonlyBoard = new ConstantBoard(this);
    }

    /** Returns a readonly version of this board. */
    Board readonlyBoard() {
        return _readonlyBoard;
    }

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        this._squares = new Square[N + 1][N + 1];
        this._numSquaresOnSide = N;
        for (int row = 1; row < N + 1; row++) {
            for (int col = 1; col < N + 1; col++) {
                _squares[row][col] = Square.INITIAL;
            }
        }
        this._undoHistory = new Stack<>();
        _readonlyBoard = new ConstantBoard(this);
    }

    /**
    * This function copies the board.
     * @param board passes in board
     * @return board returns a copied board
    */
    Board copyBoard(Board board) {
        Board boardToReturn = new Board();
        boardToReturn._numSquaresOnSide = board._numSquaresOnSide;
        int sides = _numSquaresOnSide + 1;
        boardToReturn._squares = new Square[sides][sides];
        for (int row = 1; row < _numSquaresOnSide + 1; row++) {
            for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                int spots = board.get(row, col).getSpots();
                Side sides1 = board.get(row, col).getSide();
                boardToReturn._squares[row][col] = square(sides1, spots);
            }
        }

        boardToReturn._undoHistory = new Stack<>();
        boardToReturn._undoHistory.addAll(board._undoHistory);
        _readonlyBoard = new ConstantBoard(this);
        return boardToReturn;
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        internalCopy(board);
        this._undoHistory = new Stack<>();
        this._undoHistory.addAll(board._undoHistory);
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history. Assumes BOARD and I have the same size. */
    private void internalCopy(Board board) {
        assert size() == board.size();
        this._numSquaresOnSide = board._numSquaresOnSide;
        for (int row = 1; row < _numSquaresOnSide + 1; row++) {
            for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                int spots = board.get(row, col).getSpots();
                Side sides = board.get(row, col).getSide();
                _squares[row][col] = square(sides, spots);
            }
        }
        _readonlyBoard = new ConstantBoard(this);
    }
    /** Return the number of rows and of columns of THIS. */
    int size() {
        return _numSquaresOnSide;
    }

    /** Returns the contents of the square at row R, column C
     *  1 <= R, C <= size (). */
    Square get(int r, int c) {
        return get(sqNum(r, c));
    }

    /** Returns the contents of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    Square get(int n) {
        return this._squares[row(n)][col(n)];
    }

    /** Returns the total number of spots on the board. */
    int numPieces() {
        int numPieces = 0;
        for (int row = 1; row < _numSquaresOnSide + 1; row++) {
            for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                numPieces += this._squares[row][col].getSpots();
            }
        }
        return numPieces;
    }

    /** Returns the Side of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Side whoseMove() {
        return ((numPieces() + size()) & 1) == 0 ? RED : BLUE;
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        return n / size() + 1;
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size() + 1;
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return (c - 1) + (r - 1) * size();
    }

    /** Return a string denoting move (ROW, COL)N. */
    String moveString(int row, int col) {
        return String.format("%d %d", row, col);
    }

    /** Return a string denoting move N. */
    String moveString(int n) {
        return String.format("%d %d", row(n), col(n));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     to square at row R, column C. */
    boolean isLegal(Side player, int r, int c) {
        return isLegal(player, sqNum(r, c));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Side player, int n) {
        if (!isLegal(player)) {
            return false;
        }
        if (!exists(n)) {
            return false;
        }
        if (!player.playableSquare(_squares[row(n)][col(n)].getSide())) {
            return false;
        }
        return true;
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Side player) {
        return whoseMove() == player;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Side getWinner() {
        Side winner = this._squares[1][1].getSide();

        for (int row = 1; row < _numSquaresOnSide + 1; row++) {
            for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                if (winner != this._squares[row][col].getSide()) {
                    return null;
                }
            }
        }

        if (winner == WHITE) {
            return null;
        }
        return winner;
    }

    /** Return the number of squares of given SIDE. */
    int numOfSide(Side side) {
        int numPieces = 0;

        for (int row = 1; row < _numSquaresOnSide + 1; row++) {
            for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                if (side == this._squares[row][col].getSide()) {
                    numPieces++;
                }
            }
        }
        return numPieces;
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Side player, int r, int c) {
        addSpot(player, sqNum(r, c));
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Side player, int n) {
        markUndo();
        int squares = _squares[row(n)][col(n)].getSpots() + 1;
        internalSet(row(n), col(n), squares, player);
        jump(n);
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white). */
    void set(int r, int c, int num, Side player) {
        internalSet(r, c, num, player);
        announce();
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Does not announce
     *  changes. */
    private void internalSet(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), num, player);
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white). Does not announce changes. */
    private void internalSet(int n, int num, Side player) {
        if (num > 0) {
            this._squares[row(n)][col(n)] = square(player, num);
        } else {
            this._squares[row(n)][col(n)] = Square.INITIAL;
        }
    }



    /** Undo the effects of one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {

        if (this._undoHistory.empty()) {
            return;
        }
        Square[][] poppedBoard = this._undoHistory.pop();
        for (int row = 1; row < size() + 1; row++) {
            for (int col = 1; col < size() + 1; col++) {
                this._squares[row][col] = poppedBoard[row][col];
            }
        }
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {

        Square[][] savedBoard = new Square[size() + 1][size() + 1];
        for (int row = 1; row < size() + 1; row++) {
            for (int col = 1; col < size() + 1; col++) {
                Square toCopy = this.get(row, col);
                savedBoard[row][col] = toCopy;
            }
        }
        this._undoHistory.push(savedBoard);
    }

    /** Add DELTASPOTS spots of side PLAYER to row R, column C,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int r, int c, int deltaSpots) {
        internalSet(r, c, deltaSpots + get(r, c).getSpots(), player);
    }

    /** Add DELTASPOTS spots of color PLAYER to square #N,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int n, int deltaSpots) {
        internalSet(n, deltaSpots + get(n).getSpots(), player);
    }

    /** Used in jump to keep track of squares needing processing.  Allocated
     *  here to cut down on allocations. */
    private final ArrayDeque<Integer> _workQueue = new ArrayDeque<>();

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {

        if (get(S).getSpots() <= this.neighbors(S) || getWinner() != null) {
            return;
        }
        int row = row(S);
        int col = col(S);

        int[][] neighbors = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

        int neighborRow, neighborCol;
        for (int[] neighbor : neighbors) {
            neighborRow = row + neighbor[0];
            neighborCol = col + neighbor[1];

            if (exists(neighborRow, neighborCol)) {
                simpleAdd(get(S).getSide(), neighborRow, neighborCol, 1);
                simpleAdd(get(S).getSide(), S, -1);
            }
        }

        for (int[] neighbor : neighbors) {
            neighborRow = row + neighbor[0];
            neighborCol = col + neighbor[1];

            if (exists(neighborRow, neighborCol)) {
                jump(sqNum(neighborRow, neighborCol));
            }
        }
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        Formatter out = new Formatter();

        out.format("===");
        for (int row = 1; row < _squares.length; row++) {
            out.format("\n");
            for (int col = 1; col < _squares.length; col++) {
                if (col == 1) {
                    out.format("    ");
                }
                out.format("%d", _squares[row][col].getSpots());
                switch (_squares[row][col].getSide()) {
                case WHITE:
                    out.format("- ");
                    break;
                case BLUE:
                    out.format("b ");
                    break;
                case RED:
                    out.format("r ");
                    break;
                default:
                    break;
                }
            }
        }
        out.format("\n");
        out.format("===");
        return out.toString();
    }

    /** Returns an external rendition of me, suitable for human-readable
     *  textual display, with row and column numbers.  This is distinct
     *  from the dumped representation (returned by toString). */
    public String toDisplayString() {
        String[] lines = toString().trim().split("\\R");
        Formatter out = new Formatter();
        for (int i = 1; i + 1 < lines.length; i += 1) {
            out.format("%2d %s%n", i, lines[i].trim());
        }
        out.format("  ");
        for (int i = 1; i <= size(); i += 1) {
            out.format("%3d", i);
        }
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int size = size();
        int n;
        n = 0;
        if (r > 1) {
            n += 1;
        }
        if (c > 1) {
            n += 1;
        }
        if (r < size) {
            n += 1;
        }
        if (c < size) {
            n += 1;
        }
        return n;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        } else {
            Board B = (Board) obj;

            for (int row = 1; row < _numSquaresOnSide + 1; row++) {
                for (int col = 1; col < _numSquaresOnSide + 1; col++) {
                    Side sidesA = B._squares[row][col].getSide();
                    Side sidesB = _squares[row][col].getSide();
                    int spotA = _squares[row][col].getSpots();
                    int spotB = B._squares[row][col].getSpots();
                    if (sidesA != sidesB || spotB != spotA) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        return numPieces();
    }

    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /** Take any action that has been set for a change in my state. */
    private void announce() {
        _notifier.accept(this);
    }
}
