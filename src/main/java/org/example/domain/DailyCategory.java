package org.example.domain;

import java.util.Arrays;
import java.util.List;

public class DailyCategory {
    public List<Item> items = Arrays.asList(
            new Item("Umbrella", Item.Type.UMBRELLA),
            new Item("Pen", Item.Type.PEN),
            new Item("Blanket",Item.Type.BLANKET),
            new Item("Tumbler",Item.Type.TUMBLER)
    );
}
