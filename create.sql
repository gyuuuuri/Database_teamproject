-- 1. Book 테이블
create table Book (
    book_id         int             not null AUTO_INCREMENT,
    title           varchar(255)    not null,
    author          varchar(100)    not null,
    publisher       varchar(100),
    isbn            varchar(20)     UNIQUE,
    published_date  YEAR,
    primary key (book_id)
); 

-- 2. UsedBook 테이블
create table UsedBook (
    used_book_id    int             not null AUTO_INCREMENT,
    book_id         int             not null,
    price           int   not null,
    registered_date DATETIME        not null DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('판매중','판매완료') not null DEFAULT '판매중',
    primary key (used_book_id),
    foreign key (book_id) references Book (book_id)  
    on update cascade
    on delete restrict
);

create index idx_usedbook_bookid
  ON UsedBook (book_id);

-- 3. Users 테이블
create table Users (
    user_id     int             not null,
    username    varchar(50)     not null,
    email       varchar(100)    not null,
    join_date   DATETIME            not null DEFAULT CURRENT_TIMESTAMP,
    address     varchar(255),
    points       int unsigned    not null DEFAULT 0,
    primary key (user_id)
);

-- 4. PurchaseOrder 테이블
CREATE TABLE PurchaseOrder (
    purchase_id       INT           NOT NULL AUTO_INCREMENT,
    buyer_id       INT           NOT NULL,
    purchased_date DATETIME      NOT NULL,
    -- (필요 시) 여기서 바로 계산된 total_amount 컬럼도 둬도 되고, 
    -- 필요 시 운송료/shipping_status 등을 헤더에 둡니다.
    PRIMARY KEY (order_id),
    FOREIGN KEY (buyer_id) REFERENCES Users(user_id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);
CREATE INDEX idx_order_buyer
  ON PurchaseOrder (buyer_id);

-- 5. PurchaseItem 테이블
CREATE TABLE PurchaseItem (
    purchase_id       INT           NOT NULL,
    used_book_id   INT           NOT NULL,
    final_price    INT           NOT NULL,
    reward_points  INT GENERATED ALWAYS AS (FLOOR(final_price * 0.01)) STORED,
    PRIMARY KEY (order_id, used_book_id),
    FOREIGN KEY (order_id)     REFERENCES PurchaseOrder(order_id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,
    FOREIGN KEY (used_book_id) REFERENCES UsedBook(used_book_id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);
CREATE INDEX idx_item_usedbook
  ON PurchaseItem (used_book_id);



-- 6. Shipping 테이블
create table Shipping (
    shipping_id     int             not null AUTO_INCREMENT,
    purchase_id     int             not null,
    shipping_address varchar(255)   not null,
    shipped_at      DATETIME,
    delivered_at    DATETIME,
    shipping_status ENUM('배송준비중','배송중','배송완료') not null DEFAULT '배송준비중',
    primary key (shipping_id),
    foreign key (purchase_id) references PurchaseOrder (purchase_id)
    on update cascade
    on delete restrict
);

create index idx_shipping_purchase
  on Shipping (purchase_id);


-- 뷰 정의: 사용자별 구매 내역
create view user_purchase_history as
    select
        p.purchase_id,
        p.buyer_id,
        u.username,
        p.purchased_date,
        ub.used_book_id,
        b.title       as book_title,
        p.final_price
    from Purchase p
    join Users u       on p.buyer_id     = u.user_id
    join UsedBook ub  on p.used_book_id = ub.used_book_id
    join Book b       on ub.book_id     = b.book_id;
