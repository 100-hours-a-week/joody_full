package org.example.domain;

import java.util.Arrays;
import java.util.List;

public class DailyCategory {
    public List<Item> items = Arrays.asList(
            new Item("Umbrella", Item.Type.UMBRELLA, 5),
            new Item("Pen", Item.Type.PEN, 5),
            new Item("Blanket",Item.Type.BLANKET,5 ),
            new Item("Tumbler",Item.Type.TUMBLER,5 )
    );
}
