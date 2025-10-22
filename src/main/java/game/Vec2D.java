package game;

public class Vec2D {
    public int x;
    public int y;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D add(Vec2D other) {
        return new Vec2D(x + other.x, y + other.y);
    }

    public Vec2D sub(Vec2D other) {
        return new Vec2D(x - other.x, y - other.y);
    }

    public Vec2D mul(Vec2D other) {
        return new Vec2D(x * other.x, y * other.y);
    }

    public Vec2D mul(int n) {
        return new Vec2D(n * x, n * y);
    }

    public int[] toArray() {
        return new int[] { x, y };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Vec2D other = (Vec2D) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        String formatString = "Vec2D{'x': %d, 'y': %d}";
        return String.format(formatString, x, y);
    }

    @Override
    public Vec2D clone() {
        return new Vec2D(x, y);
    }
}
