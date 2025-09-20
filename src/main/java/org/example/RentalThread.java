package org.example;

import org.example.domain.Item;
import org.example.service.RentalService;

import java.util.ArrayList;
import java.util.List;

// 여러 사용자가 같은 아이템을 동시에 빌리는 상황

public class RentalThread {
    public static void main(String[] args) {
        RentalService rentalService = new RentalService();
        List<Item> rentedItems = new ArrayList<>();

        String[] users = {"Paul","eden","Joody","ella","martin"};

        List<Thread> threads = new ArrayList<>();

        for(String user : users){
            // 대여 스레드 -> 동시에 여러 사용자가 한개의 아이템을 대여하는 상황
            Thread rentThread = new Thread(() -> {
                List<Item> items = rentalService.getElectricItems();
                if(!items.isEmpty()) {
                    Item item = items.get(0);
                    rentalService.rentItemAsync(item, user).join();
                    synchronized (rentedItems) {
                        rentedItems.add(item);
                    }
                }
            });

            // 반납 스레드
            Thread returnThread = new Thread(() -> {
                try{
                    Thread.sleep(200);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                synchronized (rentedItems) {
                    if(!rentedItems.isEmpty()) {
                        Item item = rentedItems.get(0);
                        rentalService.returnItemAsync(item, user);
                        rentedItems.remove(item);
                    }
                }
            });

            threads.add(rentThread);
            threads.add(returnThread);

            rentThread.start();
            returnThread.start();

        }
        // 모든 스레드가 끝날 때까지 기다리기
        for(Thread thread : threads){
            try {
                thread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        rentalService.shutdown();
    }
}
