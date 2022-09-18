package data.initial;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {

    private final String name; //not null, not empty
    private final Integer height; //not null, >0
    private final Country nationality; //not null
    private final Location location; //not null

    public Person(String name, Integer height, Country nationality, Location location) {
        this.name = name;
        this.height = height;
        this.nationality = nationality;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Person{"
                + "name='" + name + '\''
                + ", height=" + height
                + ", nationality=" + nationality
                + ", location=" + location
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
        Person person = (Person) o;
        return Objects.equals(name, person.name) && Objects.equals(height, person.height) && nationality == person.nationality && Objects.equals(location, person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, nationality, location);
    }
}
