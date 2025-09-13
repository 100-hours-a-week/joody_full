package org.example;
import java.util.*;

class Item{
    String name;

    Item(String name){
        this.name = name;
    }

    String getName(){
        return name;
    }
}

class ElectricItems extends Item{
    ElectricItems(String name){
        super(name);
    }
}

class DailyItems extends Item{
    DailyItems(String name){
        super(name);
    }
}

class Laptop extends ElectricItems{
    Laptop(){
        super("laptop");
    }
}

class Mouse extends ElectricItems{
    Mouse(){
        super("mouse");
    }
}

class Charger extends ElectricItems{
    Charger(){
        super("Charger");
    }
}

class headphones extends ElectricItems{
    headphones(){
        super("headphones");
    }
}

class Umbrella extends DailyItems{
    Umbrella(){
        super("Umbrella");
    }
}

class Pen extends DailyItems{
    Pen(){
        super("Pen");
    }
}

class Blanket extends DailyItems{
    Blanket(){
        super("Blanket");
    }
}

class Tumbler extends DailyItems{
    Tumbler(){
        super("Tumbler");
    }
}

class ElectricCategory{
    List<Item> items = Arrays.asList(new Laptop(), new Mouse(), new Charger(), new headphones());
}

class DailyCategory{
    List<Item> items = Arrays.asList(new Umbrella(), new Blanket(), new Tumbler(), new Pen());
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===============================================================================\n");
        System.out.println("▗▖ ▗▖▗▄▄▄▖▗▖    ▗▄▄▖ ▗▄▖ ▗▖  ▗▖▗▄▄▄▖");
        System.out.println("▐▌ ▐▌▐▌   ▐▌   ▐▌   ▐▌ ▐▌▐▛▚▞▜▌▐▌   ");
        System.out.println("▐▌ ▐▌▐▛▀▀▘▐▌   ▐▌   ▐▌ ▐▌▐▌  ▐▌▐▛▀▀▘");
        System.out.println("▐▙█▟▌▐▙▄▄▖▐▙▄▄▖▝▚▄▄▖▝▚▄▞▘▐▌  ▐▌▐▙▄▄▖");
        System.out.println("▗▖ ▗▖ ▗▄▖ ▗▖ ▗▖ ▗▄▖  ▗▄▖▗▄▄▄▖▗▄▄▄▖ ▗▄▄▖▗▖ ▗▖    ▗▄▄▖  ▗▄▖  ▗▄▖▗▄▄▄▖▗▄▄▖ ▗▄▖ ▗▖  ▗▖▗▄▄▖ ");
        System.out.println("▐▌▗▞▘▐▌ ▐▌▐▌▗▞▘▐▌ ▐▌▐▌ ▐▌ █  ▐▌   ▐▌   ▐▌ ▐▌    ▐▌ ▐▌▐▌ ▐▌▐▌ ▐▌ █ ▐▌   ▐▌ ▐▌▐▛▚▞▜▌▐▌ ▐▌");
        System.out.println("▐▛▚▖ ▐▛▀▜▌▐▛▚▖ ▐▛▀▜▌▐▌ ▐▌ █  ▐▛▀▀▘▐▌   ▐▛▀▜▌    ▐▛▀▚▖▐▌ ▐▌▐▌ ▐▌ █ ▐▌   ▐▛▀▜▌▐▌  ▐▌▐▛▀▘ ");
        System.out.println("▐▌ ▐▌▐▌ ▐▌▐▌ ▐▌▐▌ ▐▌▝▚▄▞▘ █  ▐▙▄▄▖▝▚▄▄▖▐▌ ▐▌    ▐▙▄▞▘▝▚▄▞▘▝▚▄▞▘ █ ▝▚▄▄▖▐▌ ▐▌▐▌  ▐▌▐▌   \n");
        System.out.println("카테부에서 대여용품을 준비했습니다! :) 자유롭게 이용해주세요!\n");
        System.out.println("===============================================================================");

        System.out.print("성함을 입력해주세요: ");
        String name = scanner.nextLine().trim();

        System.out.print("지원하신 과정명을 입력해주세요: ");
        String process = scanner.nextLine().trim();

        System.out.println(name + "(" + process + ")님 환영합니다!" );

        System.out.print("대여하고 싶은 카테고리를 선택해주세요. (1. 전자제품/ 2. 일상용품) ");
        int CategoryNo  = Integer.parseInt(scanner.nextLine().trim());

        List<Item> availableItems = new ArrayList<Item>();
        if(CategoryNo == 1){
            ElectricCategory cat = new ElectricCategory();
            availableItems = cat.items;
        }
        if(CategoryNo == 2){
            DailyCategory cat = new DailyCategory();
            availableItems = cat.items;
        }

        // 대여 가능 물품 리스트 및 대여 물품 신청받기
        System.out.println("대여 가능한 물품: ");
        for(int i=0;i<availableItems.size();i++){
            System.out.println((i+1)+ "." +availableItems.get(i).getName());
        }

        System.out.print("최대 2개까지 선택 가능(쉼표로 구분): ");
        String[] input = scanner.nextLine().split(",");

        while(input.length > 2){
            System.out.println("최대 2개까지만 가능합니다! 다시 선택해주세요!");
            System.out.print("최대 2개까지 선택 가능(쉼표로 구분): ");
            input = scanner.nextLine().split(",");
        }

        System.out.println(name + "(" + process + ")님 " + "대여 완료되었습니다!!");
        System.out.print("선택하신 대여 물품: ");
        for(String s: input){
            int idx = Integer.parseInt(s.trim()) - 1;
            System.out.print(availableItems.get(idx).getName() + " ");
        }
        System.out.println("입니다!");


        // 대여 물품 신청
        System.out.print("대여 물품 신청도 가능합니다. 신청하시겠습니까? (y/n): ");
        String answer = scanner.nextLine().trim();

        if(answer.equals("y")){
            System.out.print("어떤 물품을 신청하시겠습니까? ( 예) 개발도서 ) : ");
            answer = scanner.nextLine().trim();
            System.out.println("< " + answer + " >" + "이/가 신청되었습니다. 이용해주셔서 감사합니다! :) ");
        }
        if(answer.equals("n")) {
            System.out.println("이용해주셔서 감사합니다!:)");
            System.exit(0);
        }
    }
}