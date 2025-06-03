-- 데이터베이스 스키마 b: Orders 테이블 분리 버전
-- 이 스키마는 'isert_b.sql'로 생성된 데이터와 호환됩니다.

-- 외래 키 제약조건을 잠시 비활성화 (데이터 로드 시 유용)
-- SET FOREIGN_KEY_CHECKS = 0;

-- 1. Book 테이블
CREATE TABLE Book (
    book_id         INT             NOT NULL AUTO_INCREMENT,
    title           VARCHAR(255)    NOT NULL,
    author          VARCHAR(255)    NOT NULL, -- 스크립트에서 다양한 길이로 추출될 수 있어 255로 설정
    publisher       VARCHAR(100),
    isbn            VARCHAR(20)     UNIQUE,
    published_date  YEAR, -- 스크립트는 연도만 추출하여 저장
    PRIMARY KEY (book_id)
);

-- 2. UsedBook 테이블
CREATE TABLE UsedBook (
    used_book_id    INT             NOT NULL AUTO_INCREMENT,
    book_id         INT             NOT NULL,
    price           INT             NOT NULL, -- 엑셀 판매가 기반으로 스크립트에서 생성된 중고 가격
    registered_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 스크립트에서 생성
    status          ENUM('판매중','판매완료') NOT NULL DEFAULT '판매중',
    PRIMARY KEY (used_book_id),
    FOREIGN KEY (book_id) REFERENCES Book (book_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX idx_usedbook_bookid ON UsedBook (book_id);

-- 3. Users 테이블
CREATE TABLE Users (
    user_id     INT             NOT NULL, -- 스크립트에서 ID 생성
    username    VARCHAR(50)     NOT NULL,
    email       VARCHAR(100)    NOT NULL UNIQUE, -- 이메일은 고유해야 함
    join_date   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 스크립트에서 생성
    address     VARCHAR(255),
    points      INT UNSIGNED    NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id)
);

-- 4. Orders 테이블 (신규: 주문 헤더 정보)
CREATE TABLE Orders (
    order_id        INT             NOT NULL, -- 스크립트에서 주문 그룹 ID 생성
    buyer_id        INT             NOT NULL,
    order_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 스크립트에서 생성
    -- total_order_amount INT NULL, -- 필요시 주문 총액 등 추가 가능 (현재 스크립트는 계산 안 함)
    PRIMARY KEY (order_id),
    FOREIGN KEY (buyer_id) REFERENCES Users (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX idx_orders_buyer ON Orders (buyer_id);

-- 5. OrderItems 테이블 (신규: 주문 상세 정보, 기존 Purchase 역할)
CREATE TABLE OrderItems (
    order_id       INT             NOT NULL, -- Orders 테이블 참조
    used_book_id   INT             NOT NULL, -- UsedBook 테이블 참조
    final_price    INT             NOT NULL, -- UsedBook.price에서 할인 적용된 가격
    reward_points  INT             AS (FLOOR(final_price * 0.01)) STORED, -- 계산 컬럼
    PRIMARY KEY (order_id, used_book_id), -- 복합 기본키
    FOREIGN KEY (order_id) REFERENCES Orders (order_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    FOREIGN KEY (used_book_id) REFERENCES UsedBook (used_book_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX idx_orderitems_usedbook ON OrderItems (used_book_id);
-- order_id에 대한 인덱스는 Orders 테이블의 PK를 통해 간접적으로 활용되거나, 필요시 추가 가능

-- 6. Shipping 테이블 (Orders 테이블 참조)
CREATE TABLE Shipping (
    shipping_id     INT             NOT NULL AUTO_INCREMENT,
    order_id        INT             NOT NULL, -- Orders 테이블의 order_id 참조
    shipping_address VARCHAR(255)   NOT NULL,
    shipped_at      DATETIME        NULL, -- 스크립트에서 조건부 생성
    delivered_at    DATETIME        NULL, -- 스크립트에서 조건부 생성
    shipping_status ENUM('배송준비중','배송중','배송완료') NOT NULL DEFAULT '배송준비중',
    PRIMARY KEY (shipping_id),
    FOREIGN KEY (order_id) REFERENCES Orders (order_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX idx_shipping_order ON Shipping (order_id);

-- 7. 뷰 정의: 사용자별 구매 내역 (Orders 및 OrderItems 기반으로 수정)
CREATE VIEW user_purchase_history AS
    SELECT
        o.order_id,         -- Orders 테이블의 주문 ID
        o.buyer_id,         -- Orders 테이블의 구매자 ID
        u.username,         -- Users 테이블의 사용자 이름
        o.order_date,       -- Orders 테이블의 주문 날짜
        oi.used_book_id,    -- OrderItems 테이블의 중고책 ID
        b.title AS book_title, -- Book 테이블의 책 제목
        oi.final_price      -- OrderItems 테이블의 최종 가격
    FROM Orders o
    JOIN Users u            ON o.buyer_id = u.user_id
    JOIN OrderItems oi      ON o.order_id = oi.order_id
    JOIN UsedBook ub        ON oi.used_book_id = ub.used_book_id
    JOIN Book b             ON ub.book_id = b.book_id;

-- 외래 키 제약조건 다시 활성화
-- SET FOREIGN_KEY_CHECKS = 1;
