package ui;

import game.Game;
import game.Board;
import game.Vec2D;

import java.util.Scanner;

public class TUI {
    public static void main(String[] args) {
        Board board = new Board();
        board.generateSolvableStatic();
        Game game = new Game(board);
        Scanner scan = new Scanner(System.in);

        printBanner();
        printHelp();

        while (!game.finished()) {
            System.out.println(game);

            boolean askAgain = true;
            do {
                System.out.print("[l1 c1 l2 c2]> ");
                String coords[] = scan.nextLine().strip().split(" ");
                switch (coords[0]) {
                    case "m":
                        if (coords.length != 5)
                            break;
                        try {
                            Vec2D a = new Vec2D(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                            Vec2D b = new Vec2D(Integer.parseInt(coords[3]), Integer.parseInt(coords[4]));

                            if (game.merge(a, b))
                                askAgain = false;
                            else
                                System.out.println("Cannot be paired");
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect format");
                            continue;
                        }
                        break;
                    case "r":
                        int n = Integer.parseInt(coords[1]);
                        if (!game.revert(n))
                            System.out.println("Impossible to go back for the moment");
                        else
                            askAgain = false;
                        break;
                    case "h":
                        Vec2D[] h = game.hint();
                        if (h == null)
                            System.out.println("More hints for the abusive freeloaders XD");
                        else
                            System.out.println(String.format("%d %d with %d %d -- %s", h[0].x, h[0].y, h[1].x, h[1].y,
                                    "This hint is not necessary the best one ..."));
                    case "s":
                        if (coords.length != 2)
                            break;
                        game.save(coords[1]);
                        System.out.println("Saved");
                        break;
                    case "l":
                        if (coords.length != 2)
                            break;
                        if (game.load(coords[1])) {
                            System.out.println("Load");
                            askAgain = false;
                        } else
                            System.out.println("Error");
                        break;
                    case "q":
                        scan.close();
                        return;
                    case "?":
                        printHelp();
                        break;
                    default:
                        System.out.println("Wrong Command");
                        break;
                }
            } while (askAgain);
        }
        scan.close();
        System.out.println("!! Majhong Solved !!");
    }

    static void printBanner() {
        System.out.print(String.join("\n", "      +--------------------------------+",
                "      |     Majhong Text Interface     |", "      +--------------------------------+", "\n"));
    }

    static void printHelp() {
        System.out.print(String.join("\n", "Commands:", "  m [l1] [c1] [l2] [c2]  Play a move between cells",
                "                         (l1, c1) and (l2, c2)", "  r [n]                  Go back n steps",
                "  h                      Suggest a hint", "  s [fileName]           Save into a file",
                "  l [fileName]           Load a save", "  ?                      Help information",
                "  q                      Leave the program", ""));
    }
}
