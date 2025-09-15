package org.example.view;

import org.example.domain.Item;
import org.example.service.RentalService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalService rentalService = new RentalService();

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

        // 사용자 정보 입력
        System.out.print("성함을 입력해주세요: ");
        String name = scanner.nextLine().trim();

        System.out.print("지원하신 과정명을 입력해주세요: ");
        String process = scanner.nextLine().trim();

        System.out.println(name + "(" + process + ")님 환영합니다!");

        // 카테고리 선택
        System.out.print("대여하고 싶은 카테고리를 선택해주세요. (1. 전자제품/ 2. 일상용품) ");
        int categoryNo = Integer.parseInt(scanner.nextLine().trim());

        // 대여 가능한 물품 가져오기
        List<Item> availableItems;
        if (categoryNo == 1) {
            availableItems = rentalService.getElectricItems();
        } else {
            availableItems = rentalService.getDailyItems();
        }

        // 대여 가능 물품 출력
        System.out.println("대여 가능한 물품: ");
        for (int i = 0; i < availableItems.size(); i++) {
            System.out.println((i + 1) + ". " + availableItems.get(i).getName());
        }

        // 물품 선택
        System.out.print("최대 2개까지 선택 가능(쉼표로 구분): ");
        String[] input = scanner.nextLine().split(",");

        while (input.length > 2) {
            System.out.println("최대 2개까지만 가능합니다! 다시 선택해주세요!");
            System.out.print("최대 2개까지 선택 가능(쉼표로 구분): ");
            input = scanner.nextLine().split(",");
        }

        // 선택한 물품 출력
        System.out.println(name + "(" + process + ")님 " + "대여 완료되었습니다!!");
        System.out.print("선택하신 대여 물품: ");
        for (String s : input) {
            int idx = Integer.parseInt(s.trim()) - 1;
            System.out.print(availableItems.get(idx).getName() + " ");
        }
        System.out.println("입니다!");

        // 대여 물품 신청
        System.out.print("대여 물품 신청도 가능합니다. 신청하시겠습니까? (y/n): ");
        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("y")) {
            System.out.print("어떤 물품을 신청하시겠습니까? (예) 개발도서): ");
            answer = scanner.nextLine().trim();
            System.out.println("<" + answer + "> 이/가 신청되었습니다. 이용해주셔서 감사합니다! :)");
        } else {
            System.out.println("이용해주셔서 감사합니다! :)");
        }
    }
}
