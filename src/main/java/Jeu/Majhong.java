package Jeu;

import Jeu.Tuile;
import Jeu.Plateau;
import java.util.*;

public class Majhong {

    public static void main(String[] args) {
        // TODO code application logic here
        Plateau p = new Plateau();
        // p.generateRandom();
        p.generateSolvableStatic();

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println(p);

            System.out.print("[coordinates l1 c1  l2 c2]> ");
            String coords[] = scan.nextLine().split(" ");
            Vec2D a = new Vec2D(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            Vec2D b = new Vec2D(Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));

            // System.out.println("search from " + a + " to "+ b); // display input coordinates
            if (p.validMerge(a, b)) {
                System.out.println("             -- valid move --");
                p.setCase(a, Tuile.empty());
                p.setCase(b, Tuile.empty());
            } else
                System.out.println("      -- invalid move, retry --");
        }
    }

}
