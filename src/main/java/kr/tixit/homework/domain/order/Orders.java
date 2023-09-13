package kr.tixit.homework.domain.order;

import kr.tixit.homework.domain.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_no")
    private Product product;

    private Long orderQuantity;

    @Builder
    public Orders(Product product, Long orderQuantity) {
        this.product = product;
        this.orderQuantity = orderQuantity;
    }



    @Override
    public String toString() {
        return product.getProductName() + "\t-\t" + orderQuantity + "개";
    }
}
