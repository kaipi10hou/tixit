package kr.tixit.homework.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private String productName;
    private Long orderQuantity;

    @Builder
    public OrderRequestDto(String productNo, Long orderQuantity) {
        this.productName = productNo;
        this.orderQuantity = orderQuantity;
    }

    @Override
    public String toString() {
        return productName + "\t-\t" + orderQuantity + "개";
    }
}
