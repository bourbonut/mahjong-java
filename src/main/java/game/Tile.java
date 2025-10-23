package game;

public class Tile {
    public char type; // type of tile (bamboo, flower, and so on ...)
    public char id; // Identity of the tile

    Tile(char type, char id) {
        this.type = type;
        this.id = id;
    }

    public static Tile empty() {
        return new Tile('.', '.');
    } // Returns an empty tile

    public static Tile border() {
        return new Tile('.', '_');
    } // Returns a border tile

    public boolean free() {
        return type == '.';
    } // Returns true if the tile is on the border or empty

    // Displays a board in text
    @Override
    public String toString() {
        if (type == '.')
            return "..";
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
        final Tile other = (Tile) obj;
        if (this.type != other.type || this.id != other.id)
            return false;
        return true;
    }

    // Returns the array of different tiles
    public static Tile[] all() {
        Tile[] array = new Tile[3 * 9 + 4 + 3 + 4 + 4];
        int i = 0;
        for (int j = 1; j <= 9; j++)
            array[i++] = new Tile('R', (char) j);
        for (int j = 1; j <= 9; j++)
            array[i++] = new Tile('B', (char) j);
        for (int j = 1; j <= 9; j++)
            array[i++] = new Tile('C', (char) j);

        for (int j = 1; j <= 4; j++)
            array[i++] = new Tile('V', (char) j);
        for (int j = 1; j <= 3; j++)
            array[i++] = new Tile('D', (char) j);

        for (int j = 1; j <= 4; j++)
            array[i++] = new Tile('F', (char) j);
        for (int j = 1; j <= 4; j++)
            array[i++] = new Tile('S', (char) j);

        return array;
    }

    // Returns the number array of each tile type
    public static int[] number() {
        int[] array = new int[3 * 9 + 4 + 3 + 4 + 4];
        int i = 0;
        for (int j = 1; j <= 9; j++)
            array[i++] = 4;
        for (int j = 1; j <= 9; j++)
            array[i++] = 4;
        for (int j = 1; j <= 9; j++)
            array[i++] = 4;

        for (int j = 1; j <= 4; j++)
            array[i++] = 4;
        for (int j = 1; j <= 3; j++)
            array[i++] = 4;

        for (int j = 1; j <= 4; j++)
            array[i++] = 1;
        for (int j = 1; j <= 4; j++)
            array[i++] = 1;

        return array;
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

    public boolean isPair(Tile other) {
        if (this.free())
            return false;
        if (this.type == 'F' && other.type == 'F')
            return true;
        if (this.type == 'S' && other.type == 'S')
            return true;
        return this.equals(other);
    }
}
