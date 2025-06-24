package ru.skypro.hogwarts.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Faculty {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String colour;

    public Faculty(){}

    public Faculty(long id, String name, String colour) {
        this.id = id;
        this.name = name;
        this.colour = colour;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", colour='" + colour + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id && Objects.equals(name, faculty.name) && Objects.equals(colour, faculty.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, colour);
    }
}
