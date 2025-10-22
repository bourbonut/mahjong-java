package game;

import java.util.*;

public class Board implements Cloneable {

    // Board cells with tiles and border
    private Tile[][] cases;
    // Random number
    private Random rng;
    private int size;

    public Board() {
        size = 12;
        this.cases = new Tile[size + 2][size + 2]; // 12 + 2 => 2 for the border
        this.rng = new Random();
    }

    public void load(String str) {
        this.cases = new Tile[size + 2][size + 2];
        this.generateBorder();
        String s;
        int i = 1; // Index of the element
        for (int row = 1; row <= size; row++) {
            for (int col = 1; col <= size; col++) {
                s = str.substring(i, i + 2);
                if (s.equals("..")) {
                    this.cases[row][col] = Tile.empty();
                } else {
                    this.cases[row][col] = new Tile(s.charAt(0), (char) Integer.parseInt(s.substring(1)));
                }
                i += 3; // to skip "XX," where XX can be R9 for instance
            }
        }
        this.generateBorder();
    }

    public String save() {
        String str = "[";
        for (int row = 1; row <= size; row++) {
            for (int col = 1; col <= size; col++) {
                str += this.cases[row][col] + ",";
            }
        }
        str += "]";
        return str;
    }

    // Builds the board with tiles randomly arranged in the board
    public void generateRandom() {
        // Tile type array
        Tile[] types = Tile.all();
        // Disponibility array given specified tile type
        int[] disponibilites = Tile.number();

        for (int row = 1; row <= size; row++) {
            for (int col = 1; col <= size; col++) {
                int index = 0;
                do {
                    index = rng.nextInt(types.length);
                } while (disponibilites[index] <= 0);

                this.cases[row][col] = types[index];
                disponibilites[index]--;
            }
        }
        this.generateBorder();
    }

    // Fills the border with empty tiles
    public void generateBorder() {
        Tile border = Tile.border();
        for (int i = 0; i < size + 2; i++) {
            this.cases[0][i] = border;
        }
        for (int i = 0; i < size + 2; i++) {
            this.cases[size + 1][i] = border;
        }
        for (int i = 1; i <= size; i++) {
            this.cases[i][0] = border;
            this.cases[i][size + 1] = border;
        }
    }

    // Generates a solvable board from a specified random board
    public void generateSolvableStatic() {
        this.generateRandom();
        this.correct();
    }

    // Fix the board to be solvable
    public void correct() {
        Solver s = new Solver((Board) this.clone());
        // int solvedIteration = 0;
        while (!s.finished()) { // Use a solver to solve the game

            // cherche a retirer une paire
            Vec2D[] merge = s.nextMerge();
            if (merge != null) {
                s.merge(merge[0], merge[1]);
                // solvedIteration++;
            } else {
                // System.out.println("No solution after " + solvedIteration + " steps, changed board");
                // No available pair, make it one
                merge = s.findPair();
                // Take a filled cell on next to one of tiles
                // Note: It does not make board hard to solve (pairs are adjacents)
                for (Vec2D side : Board.sides()) {
                    Vec2D near = merge[0].add(side);
                    if (!this.getCase(near).free()) {
                        // Swap the found cell with the second pair
                        this.swap(near, merge[1]);
                        s.swap(near, merge[1]);
                        // System.out.println(" swap " + near + " " + merge[1]);
                        break;
                    }
                    near = merge[1].add(side);
                    if (!this.getCase(near).free()) {
                        // Swap the found cell with the second pair
                        this.swap(near, merge[0]);
                        s.swap(near, merge[0]);
                        // System.out.println(" swap " + near + " " + merge[0]);
                        break;
                    }
                }
            }
        }
    }

    // Build a board with the line method
    public void generateSolvableStaticLine() {
        Tile[] types = Tile.all(); // Tile type array
        int[] disponibilites = Tile.number(); // Disponibility array given specified tile type

        // Current position courante of the first placed tile
        Vec2D pos;
        // Current position of the second placed tile
        Vec2D posPair;
        int[] coords;

        ArrayList<Vec2D> inserer = new ArrayList<>();
        int index;
        int direction;
        int ligne = rng.nextInt(size) + 1;
        int colonne = rng.nextInt(size) + 1;

        // First placed tile
        index = rng.nextInt(types.length);
        this.cases[ligne][colonne] = types[index];
        disponibilites[index]--;

        pos = new Vec2D(ligne, colonne);
        inserer.add(pos);

        // Second tile placement
        direction = this.generatingFirstDirection(pos);
        posPair = this.generatingFirstMovement(pos, direction);
        coords = posPair.toArray();
        this.cases[coords[0]][coords[1]] = types[index];
        index = this.disponibilities(index, disponibilites);
        disponibilites[index]--;
        inserer.add(posPair);

        int[] posAndDir = new int[3];

        for (int i = 1; i < 72; i++) {
            do {// Random generation of a available tile
                index = rng.nextInt(types.length);
            } while (disponibilites[index] <= 0);

            posAndDir = this.generatingNewPosition(inserer);

            // If the random choice does not allow to fill the two last cells,
            // it restarts until it works
            if (posAndDir.length == 1) {
                this.cases = new Tile[size + 2][size + 2];
                this.generateSolvableStaticLine();
                break;
            }

            pos = new Vec2D(posAndDir[0], posAndDir[1]);
            direction = posAndDir[2];

            this.cases[pos.x][pos.y] = types[index];
            disponibilites[index]--;

            index = this.disponibilities(index, disponibilites);

            posPair = this.generatingEvenPosition(pos, direction);
            this.cases[posPair.x][posPair.y] = types[index];
            disponibilites[index]--;

            inserer.add(pos);
            inserer.add(posPair);
        }
        // Generation of a border
        this.generateBorder();
    }

    // Here, all methods are linked to generateStaticLine
    // Generates a random direction (the first one)
    int generatingFirstDirection(Vec2D pos) {
        Vec2D next; // Tested position
        Vec2D sides[] = this.sides();
        this.rng = new Random();
        int direction;
        do {
            direction = rng.nextInt(4);
            next = pos.add(sides[direction]);
        } while (this.belongBorder(next));
        return direction;
    }

    // Generates a random position given the specified direction (the first one)
    Vec2D generatingFirstMovement(Vec2D pos, int direction) {
        // Position testée
        Vec2D next;
        Vec2D sides[] = this.sides();
        int distance;
        switch (direction) {
            case 0:
                distance = pos.x;
                break;
            case 1:
                distance = 12 - pos.x;
                break;
            case 2:
                distance = 12 - pos.y;
                break;
            default:
                distance = pos.y;
                break;
        }
        this.rng = new Random();
        int pas;
        do {
            pas = rng.nextInt(distance);
            next = pos.add(sides[direction].mul(new Vec2D(pas, pas)));
        } while (pas == 0 || this.belongBorder(next));
        return next;
    }

    // Useful for the second placed tile
    // Method to distinguish between "generic" tiles and unique ones
    // If generic tile: unmodified index
    // If unique tile: returns the index of the same *family* and yet available
    int disponibilities(int index, int[] dispo) {
        if (dispo[index] > 0) {
            return index;
        }
        this.rng = new Random();
        if (index >= 34 && index < 38) {
            do {
                index = 34 + rng.nextInt(4);
            } while (dispo[index] <= 0);
        } else if (index >= 38 && index < dispo.length) {
            do {
                index = 38 + rng.nextInt(4);
            } while (dispo[index] <= 0);
        }
        return index;
    }

    // Returns the opposite direction
    int opposedDirection(int direction) {
        int oppose = 0;
        switch (direction) {
            case 0:
                oppose = 1;
                break;
            case 1:
                oppose = 0;
                break;
            case 2:
                oppose = 3;
                break;
            case 3:
                oppose = 2;
                break;
            default:
                break;
        }
        return oppose;
    }

    // Returns true if filled cells around the (non-empty) position exist
    boolean checkingAroundCase(Vec2D pos, int direction) {
        if (this.getCase(pos) != null) {
            return false;
        }
        Vec2D sides[] = this.sides();

        // We don't want to check the last cell
        sides[this.opposedDirection(direction)] = new Vec2D(0, 0);

        int i = 0;
        Vec2D next = null;
        boolean condition = false;
        while (i < 4 && !condition) {
            next = pos.add(sides[i]);
            if (this.getCase(next) != null) {
                condition = true;
            } else {
                i++;
            }
        }
        return condition;
    }

    // Generates a distance on which we search a position of the second tile
    ArrayList<Integer> generatingDistance(Vec2D pos, int direction) {
        ArrayList<Integer> distance = new ArrayList<>();
        switch (direction) {
            case 0:
                for (int i = 1; i < pos.x; i++) {
                    distance.add(i);
                }
                break;
            case 1:
                for (int i = 1; i <= (12 - pos.x); i++) {
                    distance.add(i);
                }
                break;
            case 2:
                for (int i = 1; i <= (12 - pos.y); i++) {
                    distance.add(i);
                }
                break;
            case 3:
                for (int i = 1; i < pos.y; i++) {
                    distance.add(i);
                }
                break;
        }
        return distance;
    }

    // Generates a position which checks the line method
    int[] generatingNewPosition(ArrayList<Vec2D> inserer) {
        ArrayList<Vec2D> insererClone = (ArrayList<Vec2D>) inserer.clone();
        this.rng = new Random();
        Vec2D next = null;
        Vec2D sides[] = this.sides();
        boolean condition = false;
        int iVec;
        Vec2D pos;
        int direction = -1;
        // Search a new position
        while (!condition || direction == -1 || insererClone.isEmpty()) {
            if (insererClone.isEmpty()) {
                break;
            }
            condition = false;
            // Possibilities array
            ArrayList<Integer> possibilites = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                possibilites.add(i);
            }
            // We take a random vector from already placed ones
            iVec = rng.nextInt(insererClone.size());
            pos = insererClone.get(iVec);
            insererClone.remove(iVec);
            int rand;
            int i = 0;
            while (!possibilites.isEmpty() && !condition) {
                rand = rng.nextInt(possibilites.size());
                next = pos.add(sides[possibilites.get(rand)]);
                possibilites.remove(rand);
                condition = (this.getCase(next) == null && !this.belongBorder(next));
            }
            if (condition) {
                direction = this.generatingNewDirection(next);
            } else {
                direction = -1;
            }
        }
        // Case when we cannot fill the two last cells
        if (insererClone.isEmpty()) {
            return new int[1];
        }

        return new int[] { next.x, next.y, direction };
    }

    // Generates a "filled" direction on the specified position
    int generatingNewDirection(Vec2D pos) {
        this.rng = new Random();
        Vec2D next = null;
        Vec2D sides[] = this.sides();
        int direction = 0;
        int iDirection = 0;
        ArrayList<Integer> directions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            directions.add(i);
        }
        boolean condition1 = false; // true if a filled cell exists given the
        // specified direction or a filled cell exists around an empty cell
        boolean condition2 = false; // true if an empty cell exists

        while (!(condition1 && condition2) && !directions.isEmpty()) {
            condition1 = false;
            condition2 = false;
            do { // find a possible direction
                if (directions.isEmpty()) {
                    break;
                }
                iDirection = rng.nextInt(directions.size());
                direction = directions.get(iDirection);
                next = pos.add(sides[direction]);
                directions.remove(iDirection);
            } while (this.belongBorder(next));

            //
            next = pos.add(sides[direction]);
            while (!this.belongBorder(next) && !(condition1 && condition2)) {
                // True if filled cell
                if (this.cases[next.x][next.y] != null) {
                    condition1 = true;
                }
                // True if empty cell
                if (this.cases[next.x][next.y] == null) {
                    condition2 = true;
                    // If an empty cell exists around an empty cell, condition 1 is valid
                    if (this.checkingAroundCase(next, direction)) {
                        condition1 = true;
                    }
                }
                next = next.add(sides[direction]);
            }
        }
        // Case when all directions are wrong
        if (directions.isEmpty()) {
            direction = -1;
        }
        return direction;
    }

    // Generates a pair position given a specified direction and a specified position
    Vec2D generatingEvenPosition(Vec2D pos, int direction) {
        this.rng = new Random();
        Vec2D next = null;
        Vec2D sides[] = this.sides();
        // Step building: we start by initializing a distance between
        // the tile position and the board border
        ArrayList<Integer> distance = this.generatingDistance(pos, direction);
        int pas;
        int iPas = 0;
        Vec2D posPair = null;
        boolean condition = false;
        while (!condition) {
            int i = 0;
            do {// we search a already place tile in the chosen direction
                // or an empty tile surrounded by filled tiles
                iPas = rng.nextInt(distance.size());
                pas = distance.get(iPas);
                distance.remove(iPas);
                next = pos.add(sides[direction].mul(pas));
            } while (!this.checkingAroundCase(next, direction) && this.getCase(next) == null);

            if (this.getCase(next) == null) {
                posPair = next;
                condition = true;
            } else {
                while (i < 2 && !condition) {
                    // We search an empty tile on the chosen tile border
                    switch (i) {
                        case 0:
                            posPair = next.add(sides[direction].mul(-1));
                            condition = (this.getCase(posPair) == null && !this.belongBorder(posPair));
                            break;
                        case 1:
                            posPair = next.add(sides[direction]);
                            condition = (this.getCase(posPair) == null && !this.belongBorder(posPair));
                            break;
                    }
                    i++;
                }
            }
        }
        return posPair;
    }

    // Returns the direction array directly accessible from a cell
    public static Vec2D[] sides() {
        return new Vec2D[] { new Vec2D(-1, 0), new Vec2D(1, 0), new Vec2D(0, 1), new Vec2D(0, -1), };
    }

    public Tile getCase(Vec2D position) {
        return this.cases[position.x][position.y];
    }

    public void setCell(Vec2D position, Tile toset) {
        this.cases[position.x][position.y] = toset;
    }

    public int getSize() {
        return size;
    }

    // Swap two tile
    public void swap(Vec2D a, Vec2D b) {
        Tile temp = this.getCase(a);
        this.setCell(a, this.getCase(b));
        this.setCell(b, temp);
    }

    public void merge(Vec2D a, Vec2D b) {
        this.setCell(a, Tile.empty());
        this.setCell(b, Tile.empty());
    }

    // Returns true if the specified vector matchs a cell on the border
    public boolean belongBorder(Vec2D vector) {
        return (vector.x == 13 || vector.x == 0 || vector.y == 13 || vector.y == 0);
    }

    // Checks if the move is possible 
    // verifier si un coup est possible entre 2 cases
    public boolean validMerge(Vec2D a, Vec2D b) {
        // Checks if the tiles are equivalent
        // verification de si les tuiles se correspondent
        if (!this.getCase(a).appairable(this.getCase(b))) {
            return false;
        }
        // System.out.println("search");

        // Possible directions
        Vec2D sides[] = this.sides();

        // Taken path to reach to the current step. It works like a stack
        ArrayList<Integer> path = new ArrayList<>(); // Taken direction array to reach to each step

        // Variables of the current step
        // path.add(-1);
        int side = 0; // Testing direction
        Vec2D pos = a; // Current position
        int coudes = 0; // Number of shifts in the current path

        while (true) {
            boolean no_side_found = true;

            // Checks if B is in the nearest neighbors
            for (int i = side; i < sides.length; i++) {
                Vec2D next = pos.add(sides[i]);
                int lastdir = path.size() - 1;
                if (next.equals(b) && (coudes < 2 || (coudes <= 2 && lastdir >= 0 && i == path.get(lastdir)))) {
                    return true;
                }
            }

            for (; side < sides.length; side++) {
                Vec2D next = pos.add(sides[side]);
                int max = this.size + 1;
                if (next.x > max || next.y > max || next.x < 0 || next.y < 0) {
                    continue;
                }

                Tile t = this.getCase(next);
                if (t.free()) {
                    if (coudes < 2 || (path.size() > 0 && side == path.get(path.size() - 1))) {
                        // Count the shift
                        if (path.size() > 0 && side != path.get(path.size() - 1)) {
                            coudes++;
                        }
                        // Stacks the new direction
                        path.add(side);
                        pos = next;
                        side = 0;
                        no_side_found = false;
                        break;
                    }
                }
            }
            if (no_side_found) {
                do {
                    if (path.size() <= 0) {
                        return false;
                    } // If the path is empty, there is no possible path
                    // Unstacks the path
                    int last = path.size() - 1;
                    side = path.get(last);
                    pos = pos.sub(sides[side]);
                    path.remove(last);
                    // decompter le coude
                    if (path.size() > 0 && side != path.get(path.size() - 1)) {
                        coudes--;
                    }

                    side++; // Checks if the next side
                } while (side >= sides.length);
            }
        }

        // return false; // If the stack is empty, no path is possible
    }

    @Override
    public String toString() {
        String display = new String();
        display += "     01 02 03 04 05 06 07 08 09 10 11 12      " + "\n";
        display += "   +-------------------------------------+    " + "\n";
        for (int ligne = 1; ligne <= size; ligne++) {
            String l;
            if (ligne < 10) {
                l = "0" + ligne;
            } else {
                l = "" + ligne;
            }
            display += l + " | ";
            for (int colonne = 1; colonne <= size; colonne++) {
                display += this.cases[ligne][colonne].toString() + " ";
            }
            display += "| " + l + "\n";
        }
        display += "   +-------------------------------------+    " + "\n";
        display += "     01 02 03 04 05 06 07 08 09 10 11 12      ";
        return display;
    }

    public Object clone() {
        Board p = null;
        try {
            p = (Board) super.clone();
        } catch (CloneNotSupportedException cnse) {
            cnse.printStackTrace(System.err); // Should never append because the Cloneable interface is implemented
        }

        p.size = this.size;
        p.cases = new Tile[this.size + 2][this.size + 2];
        for (int i = 0; i < this.size + 2; i++) {
            for (int j = 0; j < this.size + 2; j++) {
                p.cases[i][j] = this.cases[i][j];
            }
        }
        // We returns a clone
        return p;
    }
}
//
