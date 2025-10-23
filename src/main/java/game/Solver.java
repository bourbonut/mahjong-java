package game;

import java.util.ArrayList;

/**
 * Solver class which solve a board step by step. It can also be used as a tool to genrate a safe solvable board. In the
 * current state, there are some boards that the solver cannot solve because it ends in a unresolvable situation but
 * they are minority cases.
 */
public class Solver {
    Board board;
    int onBoard;

    public Solver(Board p) {
        this.board = p;
        int size = p.getSize();

        // Count tiles on the board
        this.onBoard = 0;
        Vec2D pos = new Vec2D(1, 1);
        for (pos.x = 1; pos.x <= size; pos.x++) {
            for (pos.y = 1; pos.y <= size; pos.y++) {
                if (!p.getCell(pos).isFree())
                    this.onBoard++;
            }
        }
    }

    // Search a tile couple to *pair* (for playing a move)
    public Vec2D[] nextMerge() {
        Vec2D[] couple = nextMergeDistant();
        if (couple == null)
            return nextMergeNear();
        else
            return couple;
    }

    // Search a side by side tile couple to *pair*
    public Vec2D[] nextMergeNear() {
        int size = board.getSize();
        Vec2D p = new Vec2D(1, 2);
        for (; p.x < size; p.x++) {
            for (p.y = 1; p.y <= size; p.y++) {
                Vec2D near = new Vec2D(p.x, p.y - 1);
                if (board.getCell(near).isPair(board.getCell(p)))
                    return new Vec2D[] { near, p };
                near = new Vec2D(p.x - 1, p.y);
                if (board.getCell(near).isPair(board.getCell(p)))
                    return new Vec2D[] { near, p };
            }
        }
        return null; // no closed pair found
    }

    // Find a distant tile couple to *pair* (with valid path)
    public Vec2D[] nextMergeDistant() {
        int size = board.getSize();
        Vec2D[] sides = Board.sides();
        // Find a filled cell on border (adjacent to empty cell)
        Vec2D a = new Vec2D(1, 1);

        do {
            a = findNonFree(a);
            if (a == null)
                break;
            for (Vec2D side : sides) {
                if (board.getCell(a.add(side)).isFree()) {
                    Vec2D b = a.clone();
                    do {
                        b = findPairOf(a, b);
                        if (b != null) {
                            // Search a path between `a` and `b`, by beginning
                            // with B (checks also that `b` is on border)
                            if (board.validMerge(b, a))
                                return new Vec2D[] { a, b };
                        }
                    } while (b != null);
                    break;
                }
            }
            a.y++;
        } while (a.y <= size || a.x <= size);

        return null;
    }

    // Old method, non optimized, unfinished to debug
    public Vec2D[] nextMergeDistant_frontier() {
        int size = board.getSize();
        // Arranged directions (here, anti-clockwise)
        Vec2D[] sides = new Vec2D[] { new Vec2D(1, 0), new Vec2D(1, 1), new Vec2D(0, 1), new Vec2D(-1, 1),
                new Vec2D(-1, 0), new Vec2D(-1, -1), new Vec2D(0, -1), new Vec2D(1, -1), };
        ArrayList<Vec2D> candidats = new ArrayList<>();

        // Finds hole borders
        Vec2D p = new Vec2D(1, 1);
        Vec2D previous = p;
        Vec2D next = p;
        for (; p.x <= size; p.x++) {
            for (p.y = 1; p.y <= size; p.y++) {

                previous = next = p;
                candidats = new ArrayList<>();
                // Follow the free/occupied border, the loop does not follow if
                // not on the border
                do {
                    int s = 0;
                    boolean free = board.getCell(next.add(sides[s++])).isFree();
                    while (s < sides.length) {
                        Vec2D possibility = next.add(sides[s++]);
                        if (board.getCell(possibility).isFree() != free) {
                            if (possibility.equals(previous))
                                free = !free;
                            else {
                                previous = next;
                                next = possibility;
                                break;
                            }
                        }
                    }
                    if (s >= sides.length)
                        break; // no border, must find a new one

                    if (!free)
                        next = previous.add(sides[s - 2]);

                    // s is now a filled cell on border
                    // Finds the corresponding candidate
                    for (int i = 0; i < candidats.size(); i++) {
                        Vec2D c = candidats.get(i);
                        if (board.getCell(c).isPair(board.getCell(next))) {
                            // tester si il y a un chemin valide
                            if (board.validMerge(next, c))
                                return new Vec2D[] { next, c };
                        }
                    }
                    // No valid pair, skip
                    candidats.add(next);

                } while (!next.equals(p));
            }
        }

        // At this point, no pair was found
        return null; // null to not valid the returned vector
    }

    // Finds a occupied cell by a tile
    public Vec2D findNonFree(Vec2D start) {
        Vec2D a = (Vec2D) start.clone();
        int size = board.getSize();
        for (; a.x <= size; a.x++) {
            for (; a.y <= size; a.y++) {
                if (!board.getCell(a).isFree())
                    return a;
            }
            a.y = 1;
        }
        return null;
    }

    // Returns the tile corresponding to the one specified by coordinates
    public Vec2D findPairOf(Vec2D a, Vec2D start) {
        int size = board.getSize();
        Vec2D b = new Vec2D(start.x, start.y + 1);
        for (; b.x <= size; b.x++) {
            for (; b.y <= size; b.y++) {
                if (board.getCell(a).isPair(board.getCell(b)))
                    return b;
            }
            b.y = 1;
        }
        return null;
    }

    // Returns a pair of valid tiles
    public Vec2D[] findPair() {
        Vec2D a = findNonFree(new Vec2D(1, 1));
        Vec2D b = null;
        if (a != null)
            b = findPairOf(a, a);
        if (b != null)
            return new Vec2D[] { a, b };
        return null;
    }

    // Returns true if the board is empty
    public boolean finished() {
        return onBoard <= 0;
    }

    // Convinient method inspired by `Board.merge`
    public void merge(Vec2D a, Vec2D b) {
        this.board.merge(a, b);
        this.onBoard -= 2;
    }

    public void swap(Vec2D a, Vec2D b) {
        this.board.swap(a, b);
    }

    @Override
    public String toString() {
        return this.board.toString();
    }

    public int getOnBoard() {
        return this.onBoard;
    }

    // For testing purpose: display the resolution steps of a board, stops in failing case
    public static void main(String[] args) {
        System.out.println("test Solver.main ...\n");
        Board board = new Board();

        board.generateSolvableStatic();
        System.out.println("Starting board:\n" + board + "\n");

        Solver solver = new Solver(board);

        while (!solver.finished()) {
            System.out.println("\n" + solver.board);
            // Search to remove a pair
            Vec2D[] merge = solver.nextMerge();
            if (merge != null) {
                System.out.println(" >> Pair found: " + merge[0] + " " + merge[1]);
                solver.merge(merge[0], merge[1]);
            } else {
                System.out.println("\nNo solution found.");
                return;
            }
        }
        System.out.println("\nBoard solved.");
    }
}
