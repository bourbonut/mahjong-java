package Jeu;

import java.util.ArrayList;
import java.util.Random;

/**
 * Solver class which solve a board step by step. It can also be used as a tool to genrate a safe solvable board.
 * In the current state, there are some boards that the solver cannot solve because it ends in a unresolvable situation
 * but they are minority cases.
 */
public class Solveur {
    // int random_order[]; // board iteration order, for resolutions, allows to make the generation more complicated
    Plateau plat;
    int onBoard;

    public Solveur(Plateau p) {
        this.plat = p;
        int taille = p.getTaille();

        // compte des tuiles sur le plateau
        this.onBoard = 0;
        Vec2D pos = new Vec2D(1, 1);
        for (pos.x = 1; pos.x <= taille; pos.x++) {
            for (pos.y = 1; pos.y <= taille; pos.y++) {
                if (!p.getCase(pos).free())
                    this.onBoard++;
            }
        }
        // Random rng = new Random();
        // for (int i=0; i<taille; i++) this.random_order[i] = rng.nextInt(taille);
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
        int size = plat.getTaille();
        Vec2D p = new Vec2D(1, 2);
        for (; p.x < size; p.x++) {
            for (p.y = 1; p.y <= size; p.y++) {
                Vec2D near = new Vec2D(p.x, p.y - 1);
                if (plat.getCase(near).appairable(plat.getCase(p)))
                    return new Vec2D[] { near, p };
                near = new Vec2D(p.x - 1, p.y);
                if (plat.getCase(near).appairable(plat.getCase(p)))
                    return new Vec2D[] { near, p };
            }
        }
        return null; // no closed pair found
    }

    // Find a distant tile couple to *pair* (with valid path)
    public Vec2D[] nextMergeDistant() {
        int size = plat.getTaille();
        Vec2D[] sides = Plateau.sides();
        // Find a filled cell on border (adjacent to empty cell)
        Vec2D a = new Vec2D(1, 1);

        do {
            a = findNonFree(a);
            if (a == null)
                break;

            for (Vec2D side : sides) {
                if (plat.getCase(a.add(side)).free()) {
                    // System.out.println("analyze "+ plat.getCase(a));
                    Vec2D b = (Vec2D) a.clone();
                    do {
                        b = findPairOf(a, b);
                        if (b != null) {
                            // System.out.println("found pair " + b);
                            // Search a path between `a` and `b`, by beginning
                            // with B (checks also that `b` is on border)
                            if (plat.validMerge(b, a))
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
        int size = plat.getTaille();
        Vec2D[] sides = new Vec2D[] { // Arranged directions (here, anti-clockwise)
                new Vec2D(1, 0), new Vec2D(1, 1), new Vec2D(0, 1), new Vec2D(-1, 1), new Vec2D(-1, 0),
                new Vec2D(-1, -1), new Vec2D(0, -1), new Vec2D(1, -1), };
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
                // System.out.println("start at "+ p);
                do {
                    int s = 0;
                    boolean free = plat.getCase(next.add(sides[s++])).free();
                    while (s < sides.length) {
                        Vec2D possibility = next.add(sides[s++]);
                        if (plat.getCase(possibility).free() != free) {
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
                        if (plat.getCase(c).appairable(plat.getCase(next))) {
                            // tester si il y a un chemin valide
                            if (plat.validMerge(next, c))
                                return new Vec2D[] { next, c };
                        }
                    }
                    // pas d'appairage valide, passer à la suite
                    candidats.add(next);

                } while (!next.equals(p));
            }
        }

        // At this point, no pair was found
        return null; // null to not valid the returned vector
    }

    // Finds a occupied cell by a tile
    // trouve une case occupée par une tuile
    public Vec2D findNonFree(Vec2D start) {
        Vec2D a = (Vec2D) start.clone();
        int size = plat.getTaille();
        for (; a.x <= size; a.x++) {
            for (; a.y <= size; a.y++) {
                if (!plat.getCase(a).free())
                    return a;
            }
            ;
            a.y = 1;
        }
        return null;
    }

    // Returns the tile corresponding to the one specified by coordinates
    public Vec2D findPairOf(Vec2D a, Vec2D start) {
        int size = plat.getTaille();
        Vec2D b = new Vec2D(start.x, start.y + 1);
        for (; b.x <= size; b.x++) {
            for (; b.y <= size; b.y++) {
                if (plat.getCase(a).appairable(plat.getCase(b)))
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

    // Inspired methods by Board's ones
    public void merge(Vec2D a, Vec2D b) {
        this.plat.merge(a, b);
        this.onBoard -= 2;
    }

    public void swap(Vec2D a, Vec2D b) {
        this.plat.swap(a, b);
    }

    @Override
    public String toString() {
        return this.plat.toString();
    }

    public int getOnBoard() {
        return this.onBoard;
    }

    // Testing function: display the resolution steps of a `p` board, stops in failing case
    public static void testAutoSolve(Plateau p) {
        Solveur s = new Solveur(p);

        while (!s.finished()) {
            System.out.println("\n" + s.plat);
            // Search to remove a pair
            Vec2D[] merge = s.nextMerge();
            if (merge != null) {
                System.out.println(" >> couple trouvé: " + merge[0] + " " + merge[1]);
                s.merge(merge[0], merge[1]);
            } else {
                System.out.println("\npas de solution trouvée.");
                return;
            }
        }
        System.out.println("\nplateau résolu.");
    }

    public static void main(String[] args) {
        System.out.println("test Solveur.main ...\n");
        Plateau p = new Plateau();

        // p.generateRandom();
        p.generateSolvableStatic();
        // p.generateSolvableStaticLine();
        System.out.println("plateau depart:\n" + p + "\n");
        testAutoSolve(p);
    }
}
