-- 1. Book 테이블
create table Book (
    book_id         INT             NOT NULL AUTO_INCREMENT,
    title           VARCHAR(255)    NOT NULL,
    author          VARCHAR(100)    NOT NULL,
    publisher       VARCHAR(100),
    isbn            VARCHAR(20)     UNIQUE,
    published_date  YEAR,
    primary key (book_id)
); 

-- 2. UsedBook 테이블
create table UsedBook (
    used_book_id    INT             NOT NULL AUTO_INCREMENT,
    book_id         INT             NOT NULL,
    price           INT   NOT NULL,
    registered_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('판매중','예약중','판매완료') NOT NULL DEFAULT '판매중',
    primary key (used_book_id),
    foreign key (book_id) references Book (book_id)  
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

create index idx_usedbook_bookid
  ON UsedBook (book_id);

-- 3. User 테이블
create table User (
    user_id     INT             NOT NULL,
    username    VARCHAR(50)     NOT NULL,
    email       VARCHAR(100)    NOT NULL,
    join_date   DATE            NOT NULL DEFAULT CURRENT_DATE,
    address     VARCHAR(255),
    primary key (user_id)
);

-- 4. Purchase 테이블
create table Purchase (
    purchase_id    INT             NOT NULL AUTO_INCREMENT,
    used_book_id   INT             NOT NULL,
    buyer_id       INT             NOT NULL,
    purchased_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    final_price    INT   NOT NULL,
    primary key (purchase_id),
    foreign key (used_book_id) references UsedBook (used_book_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    foreign key (buyer_id) references User (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

create index idx_purchase_usedbook
  ON Purchase (used_book_id);

create index idx_purchase_buyer
  ON Purchase (buyer_id);


-- 5. Shipping 테이블
create table Shipping (
    shipping_id     INT             NOT NULL AUTO_INCREMENT,
    purchase_id     INT             NOT NULL,
    shipping_address VARCHAR(255)   NOT NULL,
    shipped_at      DATETIME,
    delivered_at    DATETIME,
    shipping_status ENUM('배송준비중','배송중','배송완료') NOT NULL DEFAULT '배송준비중',
    primary key (shipping_id),
    foreign key (purchase_id) references Purchase (purchase_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

create index idx_shipping_purchase
  ON Shipping (purchase_id);


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
    join User u       on p.buyer_id     = u.user_id
    join UsedBook ub  on p.used_book_id = ub.used_book_id
    join Book b       on ub.book_id     = b.book_id;
