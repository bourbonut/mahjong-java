package ui;

import game.Game;
import game.Board;
import game.Vec2D;

import java.util.Scanner;

public class TUI {
    public static void main(String[] args) {
        Board p = new Board();
        p.generateSolvableStatic();
        Game g = new Game(p);
        Scanner scan = new Scanner(System.in);

        printBanner();
        printHelp();

        while (!g.finished()) {
            System.out.println(g);

            boolean askAgain = true;
            do {
                System.out.print("[l1 c1 l2 c2]> ");
                String coords[] = scan.nextLine().split(" ");
                String com = coords[0];

                if (com.equals("m") && coords.length == 5) {
                    try {
                        Vec2D a = new Vec2D(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                        Vec2D b = new Vec2D(Integer.parseInt(coords[3]), Integer.parseInt(coords[4]));

                        if (g.merge(a, b))
                            askAgain = false;
                        else
                            System.out.println("Cannot be paired");
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect format");
                        continue;
                    }
                } else if (com.equals("r")) {
                    int n = Integer.parseInt(coords[1]);
                    if (!g.revert(n))
                        System.out.println("Impossible to go back for the moment");
                    else
                        askAgain = false;
                } else if (com.equals("h")) {
                    Vec2D[] h = g.hint();
                    if (h == null)
                        System.out.println("More hints for the abusive freeloaders XD");
                    else
                        System.out.println(h[0].x + " " + h[0].y + " with " + h[1].x + " " + h[1].y
                                + "     -- This hint is not necessary the best one ...");
                } else if (com.equals("s") && coords.length == 2) {
                    g.save(coords[1]);
                    System.out.println("Saved");
                } else if (com.equals("l") && coords.length == 2) {
                    if (g.load(coords[1])) {
                        System.out.println("Load");
                        askAgain = false;
                    } else
                        System.out.println("Error");
                } else if (com.equals("?"))
                    printHelp();
                else if (com.equals("q"))
                    return;
                else
                    System.out.println("Wrong Command");
            } while (askAgain);
            scan.close();
        }
        System.out.println("!! Majhong Solved !!");
    }

    static void printBanner() {
        System.out.print("\n" + "      +--------------------------------+       \n"
                + "      |    Majhong Text Interface      |       \n"
                + "      +--------------------------------+       \n");
    }

    static void printHelp() {
        System.out.print("commands:\n" + "   m [l1] [c1] [l2] [c2]     play a move between cells (l1, c1) et (l2, c2)\n"
                + "   r [n]                     go back n steps\n" + "   h                         suggest a hint\n"
                + "   s [nom_fichier]           save into a file\n" + "   l [nom_fichier]           load a save\n"
                + "   ?                         help information\n"
                + "   q                         leave the program\n");
    }
}
