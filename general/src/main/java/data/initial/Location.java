package data.initial;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

    private final float x;
    private final long y;
    private final String name; //not empty, not null

    public Location(float x, long y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Location{"
                + "x=" + x
                + ", y=" + y
                + ", name='" + name + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Float.compare(location.x, x) == 0 && y == location.y && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name);
    }

    public float getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}
