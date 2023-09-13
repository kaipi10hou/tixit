package kr.tixit.homework;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import kr.tixit.homework.domain.product.Product;
import kr.tixit.homework.exception.SoldOutException;
import kr.tixit.homework.products.ProductsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsoleInputRunnerTest {

    @Autowired
    private ProductsService productsService;

    @After
    public void cleanup() {
        productsService.deleteAll();
    }

    @Before
    public void CSV_파일_읽어서_저장() {
        //given
        // CSV 파일 경로
        String csvFilePath = "src/main/resources/products/products_information.csv";

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            // CSV 파일에서 모든 레코드를 읽기
            List<String[]> records = csvReader.readAll();

            // 각 레코드에서 필드 값을 삽입
            for (String[] record : records) {
                productsService.save(Product.builder()
                        .productNo(record[0])
                        .productName(record[1])
                        .supplier(record[2])
                        .inventory(Long.valueOf(record[3]))
                        .price(Long.valueOf(record[4]))
                        .build());
            }

            //when
            List<Product> productList = productsService.findAll();

            //then
            //DB첫데이터와 csv첫데이터 비교
            Product product = productList.get(0);
            String productsStr = product.getProductNo() + " " + product.getProductName()
                    + " " + product.getSupplier() + " " + product.getInventory() + " "  + product.getPrice();
            String[] recordArr = records.get(0);
            String recordStr = recordArr[0] + " " + recordArr[1] + " " + recordArr[2] + " " + recordArr[3] + " " + recordArr[4];
            assertThat(productsStr).isEqualTo(recordStr);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSoldOutException() throws InterruptedException {
        int numThreads = 6;
        final String productNo = "L388560";
        Long orderQuantity = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        final Throwable[] exception = new Throwable[1];

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    productsService.purchase(productNo, orderQuantity);
                } catch (SoldOutException e) {
                    exception[0] = e;
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertThat(exception[0]).isInstanceOf(SoldOutException.class);
        assertThat(exception[0].getMessage()).isEqualTo("SoldOutException 발생. 주문한 상품의 수량이 재고량보다 큽니다.");
    }
}
