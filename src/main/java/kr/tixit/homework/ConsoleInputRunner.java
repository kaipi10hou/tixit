package kr.tixit.homework;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import kr.tixit.homework.domain.order.Orders;
import kr.tixit.homework.domain.order.OrdersRepository;
import kr.tixit.homework.domain.product.Product;
import kr.tixit.homework.exception.SoldOutException;
import kr.tixit.homework.products.ProductsService;
import kr.tixit.homework.web.dto.ProductRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Profile("!test")
@Component
public class ConsoleInputRunner implements CommandLineRunner {
    private final ProductsService productsService;
    private final OrdersRepository ordersRepository;

    @Autowired
    public ConsoleInputRunner(ProductsService productsService, OrdersRepository ordersRepository) {
        this.productsService = productsService;
        this.ordersRepository = ordersRepository;
    }

    @Override
    public void run(String... args) {
        setProducts();

        startCommand();
    }


    /**
     * csv 상품 정보 저장
     */
    public void setProducts() {
        //상품정보 저장
        String csvFilePath = "src/main/resources/products/products_information.csv";

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                ProductRequestDto productRequestDto = new ProductRequestDto(record);
                productsService.save(productRequestDto.toEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    /**
     * command 입력 받는 메서드
     */
    public void startCommand() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("입력(o[order]: 주문, q[quit]: 종료) : ");
        String command = scanner.nextLine();
        switch (command.toUpperCase()) {
            case "O":
                try {
                    orderProcess(scanner, printProductList());
                } catch (SoldOutException soe) {
                    System.out.println(soe.getMessage());
                }
                break;
            case "Q":
                System.out.println("고객님의 주문 감사합니다.");
                System.exit(0);
        }
        startCommand();
        scanner.close();
    }

    /**
     * 상품 정보 출력 메서드
     */
    public List<Product> printProductList() {
        List<Product> products = productsService.findAll();
        System.out.println("상품번호\t\t공급사\t\t재고\t\t가격\t\t\t상품명");
        products.forEach(productResponseDto -> System.out.println(productResponseDto.toString()));
        System.out.println("----------------------------------------------------------------------------");
        return products;
    }



    /**
     *
     * 상품 주문 입력 메서드
     * @param scanner
     * @param products
     * @return
     */
    public void orderProcess(Scanner scanner, List<Product> products) throws SoldOutException {
        while (true) {
            System.out.print("상품번호 : ");
            String inputProductNo = scanner.nextLine();
            System.out.print("수량 : ");
            String inputQuantity = scanner.nextLine();
            if ("".equals(inputProductNo) && "".equals(inputQuantity)) {
                System.out.println("주문내역 : ");
                System.out.println("----------------------------------------------------------------------------");
                //주문내용이 있는 상품의 주문리스트
                payProcess(products);
                return;
            }

            Optional<Product> orderRequest = products.stream().filter(p -> p.getProductNo().equals(inputProductNo)).findFirst();
            if (orderRequest.isPresent()) {
                List<Orders> ordersList = orderRequest.get().getOrderList();
                try {
                    Orders orders = Orders.builder()
                            .orderQuantity(Long.valueOf(inputQuantity))
                            .product(orderRequest.get())
                            .build();
                    ordersList.add(orders);
                } catch (NumberFormatException e) {
                    System.out.println("수량 입력이 잘못되었습니다.");
                }
            } else {
                System.out.println("상품번호 입력이 잘못되었습니다.");
            }
        }
    }

    /**
     * 주문 확정 처리
     */
    public void payProcess(List<Product> products) throws SoldOutException {
        Long orderAmount = 0L;
        List<List<Orders>> ordersLists = products.stream()
                .filter(p -> p.getOrderList().size() > 0)
                .map(p -> p.getOrderList()).collect(Collectors.toList());
        for (int i = 0; i < ordersLists.size(); i++) {
            List<Orders>  ol =  ordersLists.get(i);
            Long quantity = ol.stream().mapToLong(o -> o.getOrderQuantity()).sum();
            productsService.purchase(ol.get(0).getProduct().getProductNo(), quantity);
            orderAmount = quantity * ol.get(0).getProduct().getPrice();
        }

        Double paymentAmount;
        if (orderAmount >= 1000000) {
            paymentAmount = orderAmount * 0.95;
        } else {
            paymentAmount = Double.valueOf(orderAmount);
        }

        DecimalFormat df = new DecimalFormat("#,###");
        String orderAmtStr = df.format(orderAmount);
        String paymentAmtStr = df.format(paymentAmount);
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("주문금액 : " + orderAmtStr + "원");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("지불금액 : " + paymentAmtStr + "원");
        System.out.println("----------------------------------------------------------------------------");

    }
}