package Jeu;

public class Tuile {
    public char type; // type of tile (bamboo, flower, and so on ...)
    public char id; // Identity of the tile

    Tuile(char type, char id) {
        this.type = type;
        this.id = id;
    }

    public static Tuile empty() {
        return new Tuile('.', '.');
    } // Returns an empty tile

    public static Tuile border() {
        return new Tuile('.', '_');
    } // Returns a border tile

    public boolean free() {
        return type == '.';
    } // Returns true if the tile is on the border or empty

    // Displays a board in text
    @Override
    public String toString() {
        if (type == '.')
            return "..";
        // else if (type == 'F') return "F ";
        // else if (type == 'S') return "S ";
        else if (0 <= (int) id && (int) id < 10)
            return "" + type + (int) id;
        else
            return "" + type + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Tuile other = (Tuile) obj;
        if (this.type != other.type)
            return false;
        if (this.id != other.id)
            return false;
        return true;
    }

    // Returns the array of different tiles
    public static Tuile[] all() {
        Tuile[] liste = new Tuile[3 * 9 + 4 + 3 + 4 + 4];
        int i = 0;
        for (int j = 1; j <= 9; j++)
            liste[i++] = new Tuile('R', (char) j);
        for (int j = 1; j <= 9; j++)
            liste[i++] = new Tuile('B', (char) j);
        for (int j = 1; j <= 9; j++)
            liste[i++] = new Tuile('C', (char) j);

        for (int j = 1; j <= 4; j++)
            liste[i++] = new Tuile('V', (char) j);
        for (int j = 1; j <= 3; j++)
            liste[i++] = new Tuile('D', (char) j);

        for (int j = 1; j <= 4; j++)
            liste[i++] = new Tuile('F', (char) j);
        for (int j = 1; j <= 4; j++)
            liste[i++] = new Tuile('S', (char) j);

        return liste;
    }

    // Returns the number array of each tile type
    public static int[] number() {
        int[] liste = new int[3 * 9 + 4 + 3 + 4 + 4];
        int i = 0;
        for (int j = 1; j <= 9; j++)
            liste[i++] = 4;
        for (int j = 1; j <= 9; j++)
            liste[i++] = 4;
        for (int j = 1; j <= 9; j++)
            liste[i++] = 4;

        for (int j = 1; j <= 4; j++)
            liste[i++] = 4;
        for (int j = 1; j <= 3; j++)
            liste[i++] = 4;

        for (int j = 1; j <= 4; j++)
            liste[i++] = 1;
        for (int j = 1; j <= 4; j++)
            liste[i++] = 1;

        return liste;
    }

    public int dispoIndex() {
        switch (this.type) {
            case 'R':
                return (int) this.id - 1;
            case 'B':
                return 9 + (int) this.id - 1;
            case 'C':
                return 2 * 9 + (int) this.id - 1;
            case 'V':
                return 3 * 9 + (int) this.id - 1;
            case 'D':
                return 3 * 9 + 4 + (int) this.id - 1;
            case 'F':
                return 3 * 9 + 4 + 3 + (int) this.id - 1;
            case 'S':
                return 3 * 9 + 4 + 3 + 4 + (int) this.id - 1;
            default:
                return -1;
        }
    }

    public boolean appairable(Tuile other) {
        if (this.free())
            return false;
        if (this.type == 'F' && other.type == 'F')
            return true;
        if (this.type == 'S' && other.type == 'S')
            return true;
        return this.equals(other);
    }

    // Checks the tile creation
    public static void main(String[] args) {
        int[] test = Tuile.number();
        for (int i = 0; i < test.length; i++) {
            System.out.println(test[i]);
        }

        for (Tuile t : Tuile.all()) {
            System.out.println("index de " + t + ": " + t.dispoIndex());
        }
    }
}
