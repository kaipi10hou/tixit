package kr.tixit.homework.domain.product;

import kr.tixit.homework.domain.order.Orders;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "product_no")
    private String productNo;

    @Column(length = 200, nullable = false)
    private String productName;

    private String supplier;

    @Setter
    private Long inventory;

    private Long price;

    @Setter
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<Orders> orderList = new ArrayList<>();

    @Builder
    public Product(String productNo, String productName, String supplier, Long inventory, Long price) {
        this.productNo = productNo;
        this.productName = productName;
        this.supplier = supplier;
        this.inventory = inventory;
        this.price = price;
    }

    @Override
    public String toString() {
        return productNo + "\t\t" + supplier + "\t\t" + inventory + "\t\t" + price + "\t\t" + productName ;
    }
}