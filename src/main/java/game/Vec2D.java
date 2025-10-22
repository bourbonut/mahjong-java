package game;

public class Vec2D implements Cloneable {
    public int x;
    public int y;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D add(Vec2D other) {
        return new Vec2D(this.x + other.x, this.y + other.y);
    }

    public Vec2D sub(Vec2D other) {
        return new Vec2D(this.x - other.x, this.y - other.y);
    }

    public Vec2D mul(Vec2D other) {
        return new Vec2D(this.x * other.x, this.y * other.y);
    }

    public Vec2D mul(int n) {
        return new Vec2D(n * this.x, n * this.y);
    }

    public int[] toArray() {
        int[] array = new int[2];
        array[0] = this.x;
        array[1] = this.y;
        return array;
    }

    // trucs de base
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Vec2D other = (Vec2D) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "Vec2D(" + x + "," + y + ")";
    }

    @Override
    public Object clone() {
        Vec2D p = null;
        try {
            p = (Vec2D) super.clone();
        } catch (CloneNotSupportedException exc) {
            exc.printStackTrace(System.err); // Should never happen because the Cloneable interface is implemented
        }
        p.x = this.x;
        p.y = this.y;
        return p;
    }

    // test
    public static void main(String[] args) {
        // TODO code application logic here
        Vec2D v1 = new Vec2D(1, 1);
        Vec2D v2 = new Vec2D(-3, 4);
        System.out.println(v1.toString());
        System.out.println(v1.mul(v2).toString());
        System.out.println(v1.add(v2).toString());
        System.out.println(v1.sub(v2).toString());
    }
}
