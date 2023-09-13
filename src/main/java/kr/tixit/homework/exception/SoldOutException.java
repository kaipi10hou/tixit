package kr.tixit.homework.exception;

public class SoldOutException extends Exception{
    public SoldOutException() {
        super("SoldOutException 발생. 주문한 상품의 수량이 재고량보다 큽니다.");
    }

}
