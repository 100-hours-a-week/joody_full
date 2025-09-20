package org.example.domain;

import java.util.Arrays;
import java.util.List;

public class ElectricCategory {
    public List<Item> items = Arrays.asList(
            new Item("Laptop", Item.Type.LAPTOP,5),
            new Item("Mouse", Item.Type.MOUSE,5),
            new Item("Charger", Item.Type.CHARGER,5),
            new Item("headPhones", Item.Type.HEADPHONES,5)
    );
}
