package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

public class TileTest {

    @Test
    public void number() {
        int[] test = Tile.number();
        int max = 3 * 9 + 4 + 3;
        for (int i = 0; i < test.length; i++) {
            assertEquals(i < max ? 4 : 1, test[i]);
        }
    }

    @Test
    public void dispoIndex() {
        int[] numbers = Tile.number();
        Tile[] tiles = Tile.all();
        assertEquals(numbers.length, tiles.length);
        for (int i = 0; i < tiles.length; i++) {
            assertEquals(i, tiles[i].dispoIndex());
        }
    }

    @ParameterizedTest
    @CsvSource({ "F, 1, F, 1", "F, 2, F, 2", "S, 1, S, 1", "S, 1, S, 2", "R, 1, R, 1" })
    public void isPair(char type1, char id1, char type2, char id2) {
        Tile tile1 = new Tile(type1, id1);
        Tile tile2 = new Tile(type2, id2);
        assertTrue(tile1.isPair(tile2));
    }

    @ParameterizedTest
    @CsvSource({ "F, 1, R, 1", "F, 2, R, 2", "S, 1, R, 1", "S, 1, R, 2", "R, 1, R, 2" })
    public void isNotPair(char type1, char id1, char type2, char id2) {
        Tile tile1 = new Tile(type1, id1);
        Tile tile2 = new Tile(type2, id2);
        assertTrue(!tile1.isPair(tile2));
    }
}
