package org.example.view;

import org.example.domain.Item;
import org.example.service.RentalService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Main {
    // 대여, 반납 상수
    private static final int ACTION_RENT = 1;
    private static final int ACTION_RETURN = 2;

    //카테고리 상수
    private static final int CATEGORY_ELECTRICS = 1;
    private static final int CATEGORY_DAILY = 2;

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



        // 대여, 반납 선택
        System.out.print("무엇을 하시겠습니까? ( 1. 대여하기 / 2. 반납하기 ): ");
        int action = Integer.parseInt(scanner.nextLine().trim());

        // 사용자 대여 목록 관리
        Map<String, Item> rentedItems = new HashMap<>();

        if (action == ACTION_RENT) {
            // === 대여 프로세스 ===
            // 카테고리 선택
            System.out.print("대여하고 싶은 카테고리를 선택해주세요. ( 1. 전자제품 / 2. 일상용품 ) : ");
            int categoryNo = Integer.parseInt(scanner.nextLine().trim());

            // 대여 가능한 물품 가져오기
            List<Item> availableItems;
            if (categoryNo == CATEGORY_ELECTRICS) {
                availableItems = rentalService.getElectricItems();
            } else if (categoryNo == CATEGORY_DAILY) {
                availableItems = rentalService.getDailyItems();
            } else {
                System.out.println("잘못된 입력입니다.");
                rentalService.shutdown();
                return;
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



            // 선택한 대여 물품 비동기 처리
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (String s : input) {
                int idx = Integer.parseInt(s.trim()) - 1;
                Item selectedItem = availableItems.get(idx);

                CompletableFuture<Void> future = rentalService.rentItemAsync(selectedItem, name + "(" + process + ")");
                futures.add(future);

                // 대여 성공 시에만 rentedItems에 추가 -> 비동기라서 join 이후 확인
                future.thenRun(() -> {
                    if(selectedItem.getStock() >= 0){
                            rentedItems.put(selectedItem.getName(), selectedItem);
                    }
                });
            }

            // 모든 대여가 끝날 때까지 기다림.
            for(CompletableFuture<Void> f : futures) {
                try {
                    f.join();
                }catch(CompletionException e) {
                    System.out.println("비동기 작업 중 에러 발생 :" + e.getCause().getMessage());
                }
            }

            // 대여 물품 신청
            System.out.print("대여 물품 신청도 가능합니다. 신청하시겠습니까? (y/n): ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("y")) {
                System.out.print("어떤 물품을 신청하시겠습니까? (예) 개발도서): ");
                String requestItem = scanner.nextLine().trim();

                CompletableFuture<Void> requestFuture = CompletableFuture.runAsync(()-> {
                    try{
                        Thread.sleep(200);
                        System.out.println("<" + requestItem + "> 이/가 신청되었습니다. 이용해주셔서 감사합니다! :)");
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                });
                // 신청 작업이 끝날 때까지 기다림.
                requestFuture.join();

            } else {
                System.out.println("이용해주셔서 감사합니다! :)");
            }
        }
        else if (action == ACTION_RETURN) {
            // === 반납 프로세스 ===
            System.out.print("반납할 물품 이름을 입력해주세요. 예) Laptop : ");
            String returnItemName = scanner.nextLine().trim();


            // ** Optional로 존재하는 값이 있는지, 없는지 null값이 생길 수도 있어서 Optional 사용
//            Optional<Item> rented = rentedItems.stream().filter(item -> item.getName().equals(returnItemName)).findFirst(); // findFirst() 찾아낸 값에 제일 첫번째 값 가져옴.
//            if (rented.isPresent()) {
//                rentalService.returnItemAsync(rented.get(), name + "(" + process + ")");
//                rentedItems.remove(rented.get());  // 반납 완료 후 목록에서 제거
//            } else {
//                System.out.println(returnItemName + "은(는) 존재하지 않는 물품입니다. ");
//            }

            // filter로 하게 되면 한명이 수백개, 수천개의 물품을 대여하고자 할 때 리스트 전체를 순회해야 해서 비효율적일 수 있음.
            // 그래서! HashMap으로 해서 remove를 통해 반납 물품 처리 진행.
            Item rented = rentedItems.remove(returnItemName);
            if(rented != null){
                rentalService.returnItemAsync(rented, name + "(" + process + ")");
                System.out.println(returnItemName + "반납 신청 완료 !");
            }
            else {
                System.out.println(returnItemName + "은(는) 존재하지 않는 물품입니다.");
            }

        }
        else {
            System.out.println("잘못된 입력입니다. 프로그램을 종료합니다.");
        }

        // Executor 종료... 꼭 해줘야됌...!!!!!
        rentalService.shutdown();
    }
}
