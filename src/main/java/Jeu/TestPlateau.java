package Jeu;

import Jeu.Game.*;
import java.util.*;

public class TestPlateau {

    public static void main(String[] args) {
        // TODO code application logic here
        Plateau test = new Plateau();
        // test.setColonne(10);
        test.generateSolvableStaticLine();
        System.out.println(test.toString());
        Game g = new Game(test);
        g.save("Sauvegarde");
        Game a = new Game(new Plateau());
        a.load("Sauvegarde");
        System.out.println(a.toString());
        // System.out.print("Give a integer:");
        // Scanner scan = new Scanner(System.in);
        // int n1;
        // n1 = scan.nextInt();
        // System.out.print("Give a integer:");
        // int n2 = scan.nextInt();
        // System.out.print("Give a integer:");
        // int n3 = scan.nextInt();
        // System.out.print("Give a integer:");
        // int n4 = scan.nextInt();
        //
        //
        // Vec2D a = new Vec2D(n1,n2);
        // Vec2D b = new Vec2D(n3,n4);
        // System.out.println(a.toString());
        // System.out.println(b.toString());
        // System.out.println(test.getCase(a).toString());
        // System.out.println(test.getCase(b).toString());
        // System.out.println(test.validMerge(a,b));
    }
}
