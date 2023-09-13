# homework

- 개요
    - java8 / springboot2.1.7 / gradle6.4 / junit4
    - 요구사항에 따른 기능 구현
        - 상품 등록(기동과 동시 insert만. 추가 등록 기능 없음)
        - 상품 주문
        - 주문에 따른 재고 차감
- 상품정보처리
    - src/main/resource/products/products_information.csv 파일 읽어서 H2 DB insert
- 테이블 구성
    - Product
        
        ```java
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
        ```
        
    - Orders
        
        ```java
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
        ```
        
- Test
    - 단위테스트로 주문 상품 수량이 재고를 초과할 경우 SoldOutException발생 테스트
    - testSoldOutException()
        - @Before로 데이터 삽입
        - @After로 데이터 삭제
        - Exception 클래스와 Exception message로 테스트 성공 확인
        
        ```java
        assertThat(exception[0]).isInstanceOf(SoldOutException.class);
        assertThat(exception[0].getMessage()).isEqualTo("SoldOutException 발생. 주문한 상품의 수량이 재고량보다 큽니다.");
        ```
        
- 프로젝트 기동
    - src/main/java/kr/tixit/homework/Application 실행
    - src/main/java/kr/tixit/homework/ConsoleInputRunner 로 실행환경 터미널에서 Scanner로 입력받은 Command로 주문/종료 기능 수행