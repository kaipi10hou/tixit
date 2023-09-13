package kr.tixit.homework.web.dto;

import kr.tixit.homework.domain.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDto {
    private String productNo;
    private String productName;
    private String supplier;
    private Long inventory;
    private Long price;

    @Builder
    public ProductRequestDto(String[] record) {
        this.productNo = record[0];
        this.productName = record[1];
        this.supplier = record[2];
        this.inventory = Long.valueOf(record[3]);
        this.price = Long.valueOf(record[4]);
    }

    public Product toEntity() {
        return Product.builder()
                .productNo(productNo)
                .productName(productName)
                .supplier(supplier)
                .inventory(inventory)
                .price(price)
                .build();
    }
}
