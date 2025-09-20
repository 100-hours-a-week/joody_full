package org.example.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Item {
    public enum Type{
        LAPTOP, MOUSE, CHARGER, HEADPHONES,
        UMBRELLA, PEN, TUMBLER, BLANKET
    }

    // final을 붙여서 불변 객체로 만들기 위함임.
    private String name;
    private Type type;
    private final AtomicInteger stock;


    public Item(String name, Type type, int initialStock) {
        this.name = name;
        this.type = type;
        this.stock = new AtomicInteger(initialStock);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getStock() {
        return stock.get();
    }

    //대여 시도(성공하면 true, 실패하면 false)
    public boolean rent(){
        while(true){
            int current = stock.get();

            if(current <= 0){
                return false; // 재고 없음
            }
            if(stock.compareAndSet(current, current-1)){
                return true; // 성공적으로 대여
            }
        }
    }

    //반납처리
    public void returnItem(){
        stock.incrementAndGet();
    }
}
