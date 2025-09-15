package org.example.domain;

import java.util.Arrays;
import java.util.List;

public class ElectricCategory {
    public List<Item> items = Arrays.asList(
            new Item("Laptop", Item.Type.LAPTOP),
            new Item("Mouse", Item.Type.MOUSE),
            new Item("Charger", Item.Type.CHARGER),
            new Item("Headphones", Item.Type.HEADPHONES)
    );
}
