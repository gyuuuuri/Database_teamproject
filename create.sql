use bookstore;

create table Book (
    book_id         int             not null AUTO_INCREMENT,
    title           varchar(255)    not null,
    author          varchar(100)    not null,
    publisher       varchar(100),
    isbn            varchar(20)     UNIQUE,
    published_date  YEAR,
    primary key (book_id)
); 

create table UsedBook (
    used_book_id    int             not null AUTO_INCREMENT,
    book_id         int             not null,
    price           int   not null,
    registered_date DATETIME        not null DEFAULT (CURDATE()),
    status          ENUM('판매중','판매완료') not null DEFAULT '판매중',
    primary key (used_book_id),
    foreign key (book_id) references Book (book_id)  
    on update cascade
    on delete restrict
);

create index idx_usedbook_bookid
  ON UsedBook (book_id);

create table User (
    user_id     int             not null,
    username    varchar(50)     not null,
    email       varchar(100)    not null,
    join_date   DATE            not null DEFAULT (CURDATE()),
    address     varchar(255),
    primary key (user_id)
);

create table Purchase (
    purchase_id    int             not null AUTO_INCREMENT,
    used_book_id   int             not null,
    buyer_id       int             not null,
    purchased_date DATETIME        not null DEFAULT (NOW()),
    final_price    int   not null,
    reward_points  int      AS (FLOOR(final_price * 0.01)) STORED,
    primary key (purchase_id),
    foreign key (used_book_id) references UsedBook (used_book_id)
    on update cascade
    on delete restrict,
    foreign key (buyer_id) references User (user_id)
    on update cascade
    on delete restrict
);

create index idx_purchase_usedbook
  on Purchase (used_book_id);

create index idx_purchase_buyer
  on Purchase (buyer_id);


create table Shipping (
    shipping_id     int             not null AUTO_INCREMENT,
    purchase_id     int             not null,
    shipping_address varchar(255)   not null,
    shipped_at      DATETIME,
    delivered_at    DATETIME,
    shipping_status ENUM('배송준비중','배송중','배송완료') not null DEFAULT '배송준비중',
    primary key (shipping_id),
    foreign key (purchase_id) references Purchase (purchase_id)
    on update cascade
    on delete restrict
);

create index idx_shipping_purchase
  on Shipping (purchase_id);


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
