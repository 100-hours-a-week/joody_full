package org.example.service;

import org.example.*;
import org.example.domain.*;
import java.util.*;

public class RentalService {
    public List<Item> getElectricItems(){
        return Arrays.asList(new Laptop(), new Mouse(), new Charger(), new Headphones());
    }

    public List<Item> getDailyItems(){
        return Arrays.asList(new Umbrella(), new Pen(), new Blanket(), new Tumbler());
    }
}
