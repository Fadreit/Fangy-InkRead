create table user(
                     id bigint primary key auto_increment comment '用户ID',
                     username varchar(50) comment '用户名',
                     password varchar(25) comment '密码',
                     nickname varchar(50) comment '昵称',
                     phone varchar(20) comment '手机号',
                     role varchar(20)  comment '角色',
                     status tinyint  comment '状态码',
                     created_at datetime comment '注册时间',
                     updated_at datetime comment '更新时间',
                     is_deleted tinyint(1) comment '软删除'
) comment '用户表';

create table category(
                         id bigint primary key auto_increment comment '分类ID',
                         name varchar(50) comment '分类名称',
                         description varchar(255) comment '分类描述',
                         sort_order int comment '排序号（越小越靠前）',
                         book_count int comment '图书数量',
                         status tinyint comment '状态码',
                         created_at datetime comment '注册时间',
                         updated_at datetime comment '更新时间',
                         is_deleted tinyint(1) comment '软删除'
) comment '图书分类表';

create table book (
                      id bigint primary key auto_increment comment '图书ID',
                      title varchar(200) comment '书名',
                      author varchar(100) comment '作者名',
                      isbn varchar(20) comment 'ISBN号码',
                      category_id bigint comment '所属分类ID',
                      price DECIMAL(10,2) comment '价格（元）',
                      stock int comment '库存数量',
                      publisher varchar(100) comment '出版社名称',
                      publish_date DATE comment '出版日期',
                      description text comment '图书简介',
                      cover_url varchar(500) comment '封面图片URL',
                      status tinyint comment '状态码',
                      created_at datetime comment '注册时间',
                      updated_at datetime comment '更新时间',
                      is_deleted tinyint(1) comment '软删除'
) comment '图书表';

create table cart(
                     id bigint primary key auto_increment comment '购物车项ID',
                     user_id bigint comment '用户ID',
                     book_id bigint comment '图书ID',
                     quantity int comment '数量',
                     subtotal DECIMAL comment '总价',
                     created_at datetime comment '加入时间',
                     updated_at datetime comment '更新时间'
) comment '购物车表';

create table orders (
                        id bigint primary key auto_increment comment '订单ID',
                        order_no varchar(30) comment '订单号',
                        user_id bigint comment '下单用户ID',
                        total_amount decimal comment '订单总金额',
                        status varchar(20) comment '订单状态',
                        receiver_name varchar(50) comment '收货人姓名',
                        receiver_phone varchar(20) comment '收货人手机号',
                        receiver_address varchar(255) comment '收货地址',
                        logistics_company varchar(100) comment '物流公司',
                        logistics_no varchar(100) comment '物流运单号',
                        created_at datetime comment '下单时间',
                        updated_at datetime comment '更新时间'
) comment '订单表';

create table order_item(
                           id bigint primary key auto_increment comment '订单项ID',
                           order_id bigint comment '所属订单ID',
                           book_id bigint comment '图书ID',
                           book_title varchar(200) comment '图书标题快照',
                           book_cover varchar(500) comment  '图书封面快照',
                           price DECIMAL comment '下单单项图书价格',
                           quantity int comment '下单数量',
                           subtotal DECIMAL comment '单项图书总价'
) comment '订单项表';
