package org.example.service;

import org.example.domain.*;
import java.util.*;

public class RentalService {
    private ElectricCategory electricCategory = new ElectricCategory();
    private DailyCategory dailyCategory = new DailyCategory();

    public List<Item> getElectricItems() {
        return electricCategory.items;
    }
    public List<Item> getDailyItems() {
        return dailyCategory.items;
    }
}
