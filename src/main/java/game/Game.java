package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Game {
    Solver solver;
    Board board;
    int available_hints;
    boolean cheat;
    ArrayList<Board> history;

    final int initial_hints = 10;
    final int history_size = 10;

    public Game(Board board) {
        this.board = board;
        this.solver = new Solver(board);
        this.available_hints = initial_hints;
        this.history = new ArrayList<>();
        this.cheat = false;
    }

    // Loads a save file
    public Game(String file) {
        this.load(file);
    }

    // Creates a party with a board given the specified difficulty
    public Game(int difficulty) {
        this.board = new Board();
        switch (difficulty) {
            case 0: // The hardest
                this.board.generateRandom(); // Board might be unsolvable
                break;
            case 1:
                this.board.generateSolvableStatic(); // Solvable but really hard
                break;
            case 2:
                this.board.generateSolvableStaticLine(); // Solvable but easy
                break;
        }
        this.solver = new Solver(board);
        this.available_hints = initial_hints;
        this.history = new ArrayList<>();
    }

    // Gives a hint, remove a disponibililty
    public Vec2D[] hint() {
        if (this.available_hints > 0) {
            this.available_hints--;
            return solver.nextMerge();
        } else
            return null;
    }

    // Makes a move
    public boolean merge(Vec2D a, Vec2D b) {
        if (!cheat && a.equals(b))
            return false;
        if (board.validMerge(a, b)) {
            this.historyAppend(this.board);
            solver.merge(a, b);
            return true;
        } else
            return false;
    }

    // Returns true if the board is empty (so finished game)
    public boolean finished() {
        return solver.finished();
    }

    // Go back n steps (cancel moves are lost); returns true when it is possible
    public boolean revert(int n) {
        int s = this.history.size();
        if (0 < n && n <= s) {
            this.board = this.history.get(s - n);
            this.solver = new Solver(this.board);
            for (int i = s - 1; i >= s - n; i--)
                this.history.remove(i);
            return true;
        } else
            return false;
    }

    // Adds board to history
    void historyAppend(Board board) {
        history.add((Board) board.clone());
        while (history.size() > this.history_size)
            history.remove(0);
    }

    // Saves a game into the specific file
    public void save(String fileName) {
        FileWriter fichier;
        try {
            fichier = new FileWriter(fileName);
            fichier.write("Board:" + System.getProperty("line.separator"));
            fichier.write(this.board.save() + System.getProperty("line.separator"));
            fichier.write("Hints left:" + System.getProperty("line.separator"));
            fichier.write(this.available_hints + System.getProperty("line.separator"));
            fichier.write("History:" + System.getProperty("line.separator"));
            for (int i = 0; i < this.history.size(); i++) {
                fichier.write(this.history.get(i).save() + System.getProperty("line.separator"));
            }
            fichier.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Loads a party from a file, erases the current one
    public boolean load(String fileName) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName));
            String line;
            line = file.readLine();
            line = file.readLine();
            if (this.board == null)
                this.board = new Board();
            this.board.load(line);
            line = file.readLine();
            line = file.readLine();
            this.available_hints = Integer.parseInt(line);
            line = file.readLine();
            Board board;
            if (file.ready()) {// false if the history is empty
                this.history = new ArrayList<>();
                do {
                    board = new Board();
                    line = file.readLine();
                    board.load(line);
                    this.history.add(board);
                } while (file.ready());
            }
            file.close();
            this.solver = new Solver(this.board);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Board getBoard() {
        return this.board;
    }

    public int getAvailableHints() {
        return this.available_hints;
    }

    public int getHistorySize() {
        return this.history.size();
    }

    public void setCheatMode(boolean cheat) {
        this.cheat = cheat;
    }

    @Override
    public String toString() {
        return String.format("Left tiles: %d   left hints: %d   history: %d \n %s", solver.getOnBoard(), available_hints,
                history.size(), board.toString());
    }
}
