package org.example.domain;

public class Item {
    public enum Type{
        LAPTOP, MOUSE, CHARGER, HEADPHONES,
        UMBRELLA, PEN, TUMBLER, BLANKET
    }

    private String name;
    private Type type;

    public Item(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
