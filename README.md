# Create Table SQL
```sql
create table tb_product_info
(
`id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
`company_id`   varchar(30)    DEFAULT NULL COMMENT '公司ID',
`code`         varchar(11)    DEFAULT NULL COMMENT '商品ID',
`product_name` varchar(200)   DEFAULT NULL COMMENT '商品名称',
`price`        decimal(15, 2) DEFAULT NULL COMMENT '价格',
`sku_type`     tinyint(4)     DEFAULT NULL COMMENT 'sku类型',
`color_type`   tinyint(4)     DEFAULT NULL COMMENT '颜色类型',
`create_time`  datetime       DEFAULT NULL COMMENT '创建时间',
`create_date`  date           DEFAULT NULL COMMENT '创建日期',
`stock`        bigint(20)     DEFAULT NULL COMMENT '库存',
`status`       tinyint(4)     DEFAULT NULL COMMENT '状态',
PRIMARY KEY (`id`),
UNIQUE KEY `idx_code` (`code`) USING BTREE,
UNIQUE KEY `idx_sku_color` (`sku_type`, `color_type`)
) ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARSET = utf8 COMMENT ='商品信息'
```