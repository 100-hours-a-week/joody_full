package org.example.service;

import org.example.domain.*;
import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class RentalService {
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private ElectricCategory electricCategory = new ElectricCategory();
    private DailyCategory dailyCategory = new DailyCategory();
    private Map<String, List<Item>> userRantals = new HashMap<>(); // 대여한 사람의 대여 물품을 저장하기 위해 사용함.

    public List<Item> getElectricItems() {
        return electricCategory.items;
    }
    public List<Item> getDailyItems() {
        return dailyCategory.items;
    }

    // 비동기 대여 처리
    // 대여 처리에서 CompletableFuture를 쓴 이유는 보통 대여를 할 때는 내가 대여를 성공 했는지/ 실패했는지 결과가 중요해
    // 그래서 사용
    public CompletableFuture<Void> rentItemAsync(Item item, String userName) {
        return CompletableFuture.runAsync(()->{
            if(item.rent()){
                synchronized (this){
                    userRantals.putIfAbsent(userName, new ArrayList<>());
                    userRantals.get(userName).add(item);
                }
                System.out.println(userName + "님이 " + item.getName() + " 대여 성공! (남은 재고: " + item.getStock() + ")");
            } else{
                System.out.println(userName + "님, " + item.getName() + "은(는) 품절되었습니다.");
            }
        }, executor);
    }

    //비동기 반납 처리
    // 반납에서는 성공 여부가 중요하기 보다는 그냥 반납 처리만 실행하면 되서 executor.submit으로 진행
    // 반납도 동시에 여러 스레드에서 처리되도록 하기 위해
    public void returnItemAsync(Item item, String userName) {
        executor.submit(()->{
            synchronized (this){
                List<Item> rented = userRantals.getOrDefault(userName, new ArrayList<>());
                if(rented.contains(item)){
                    item.returnItem();
                    rented.remove(item);
                    System.out.println(userName + "님이 " + item.getName() + " 반납 완료! (남은 재고: " +item.getStock() + ")");

                }else{
                    System.out.println("반납 실패: "+ userName + "님은" + item.getName() + "을(를) 대여하지 않았습니다.");
                }
            }

        });
    }

    // Executor 종료
    public void shutdown(){
        executor.shutdown();
    }
}
