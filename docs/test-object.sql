use play;

drop table if exists test_object;

/*==============================================================*/
/* Table: test_object                                           */
/*==============================================================*/
create table test_object
(
   id                   int(11) not null auto_increment,
   status               varchar(20) not null,
   actual_fee           bigint(20),
   buyer_id             varchar(128),
   buy_time             datetime,
   platform_type        varchar(10) not null,
   create_time          datetime,
   update_time          datetime,
   operator_id          int(11),
   order_no             varchar(20) not null,
   primary key (id)
);

drop table if exists test_object_item;

/*==============================================================*/
/* Table: test_object_item                                      */
/*==============================================================*/
create table test_object_item
(
   id                   int(11) not null auto_increment,
   product_sku          varchar(32) not null,
   product_id           int(11) not null,
   test_object_id       int(11) not null,
   status               varchar(20),
   price                bigint(20),
   primary key (id)
);