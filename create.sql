-- 1. Book 테이블
create table Book (
    book_id         INT             NOT NULL AUTO_INCREMENT,
    title           VARCHAR(255)    NOT NULL,
    author          VARCHAR(100)    NOT NULL,
    publisher       VARCHAR(100),
    isbn            VARCHAR(20)     UNIQUE,
    published_date  DATE,
    primary key (book_id)
) 

-- 2. UsedBook 테이블
create table UsedBook (
    used_book_id    INT             NOT NULL AUTO_INCREMENT,
    book_id         INT             NOT NULL,
    price           DECIMAL(10,2)   NOT NULL,
    registered_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('available','reserved','sold') NOT NULL DEFAULT 'available',
    primary key (used_book_id),
    foreign key (book_id) references Book (book_id)  
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    INDEX idx_usedbook_bookid (book_id),
) 

-- 3. User 테이블
create table User (
    user_id     INT             NOT NULL,
    username    VARCHAR(50)     NOT NULL,
    email       VARCHAR(100)    NOT NULL,
    join_date   DATE            NOT NULL DEFAULT CURRENT_DATE,
    address     VARCHAR(255),
    primary key (user_id)
) 

-- 4. Purchase 테이블
create table Purchase (
    purchase_id    INT             NOT NULL AUTO_INCREMENT,
    used_book_id   INT             NOT NULL,
    buyer_id       INT             NOT NULL,
    purchased_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    final_price    DECIMAL(10,2)   NOT NULL,
    primary key (purchase_id),
    foreign key (used_book_id) references UsedBook (used_book_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    foreign key (buyer_id) references User (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    INDEX idx_purchase_usedbook (used_book_id),
    INDEX idx_purchase_buyer     (buyer_id),
) 

-- 5. Shipping 테이블
create table Shipping (
    shipping_id     INT             NOT NULL AUTO_INCREMENT,
    purchase_id     INT             NOT NULL,
    shipping_address VARCHAR(255)   NOT NULL,
    shipped_at      DATETIME,
    delivered_at    DATETIME,
    shipping_status ENUM('pending','shipped','delivered','returned') NOT NULL DEFAULT 'pending',
    primary key (shipping_id),
    foreign key (purchase_id) references Purchase (purchase_id)
    ON UPDATE CASCADE
    ON DELETE RESTRIC
    INDEX idx_shipping_purchase  (purchase_id),
) 
