

CREATE SEQUENCE public.category_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


CREATE SEQUENCE public.image_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.customer_order_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.customer_order_item_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


CREATE SEQUENCE public.product_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE TABLE public.category (
	id int8 NOT NULL DEFAULT nextval('category_seq'::regclass),
	"name" varchar(100) NOT NULL,
	CONSTRAINT category_pkey PRIMARY KEY (id)
);


CREATE TABLE public.product (
	id int8 NOT NULL DEFAULT nextval('product_seq'::regclass),
	long_description text NULL,
	"name" varchar(100) NOT NULL,
	price numeric(10, 2) NOT NULL,
	product_id varchar(50) NOT NULL,
	short_description varchar(255) NULL,
	stock int4 NOT NULL,
	category_id int8 NOT NULL,
	seo_description varchar(255) NULL,
	height float8 NOT NULL,
	is_active bool NOT NULL,
	length float8 NOT NULL,
	weight float8 NOT NULL,
	width float8 NOT NULL,
	CONSTRAINT product_pkey PRIMARY KEY (id),
	CONSTRAINT ukat03k6o77o1rru4e6jtn4vbx7 UNIQUE (product_id),
	CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES public.category(id)
);


CREATE TABLE public.image (
	id int8 NOT NULL DEFAULT nextval('image_seq'::regclass),
	file_name varchar(255) NOT NULL,
	"name" varchar(100) NOT NULL,
	"order" int4 NOT NULL,
	product_id int8 NOT NULL,
	CONSTRAINT image_pkey PRIMARY KEY (id),
	CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES public.product(id)
);


CREATE TYPE order_status AS ENUM ('CREATED', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED');


CREATE TABLE public.customer_order (
	id int8 NOT NULL DEFAULT nextval('customer_order_seq'::regclass),
	billing_city varchar(255) NOT NULL,
	billing_neighborhood varchar(255) NOT NULL,
	billing_number varchar(255) NOT NULL,
	billing_postal_code varchar(10) NOT NULL,
	billing_state varchar(2) NOT NULL,
	billing_street varchar(255) NOT NULL,
	cpf varchar(11) NOT NULL,
	delivery_city varchar(255) NOT NULL,
	delivery_neighborhood varchar(255) NOT NULL,
	delivery_number varchar(255) NOT NULL,
	delivery_postal_code varchar(10) NOT NULL,
	delivery_state varchar(2) NOT NULL,
	delivery_street varchar(255) NOT NULL,
	email varchar(100) NOT NULL,
	full_name varchar(100) NOT NULL,
	CONSTRAINT customer_order_pkey PRIMARY KEY (id)
);

CREATE TABLE public.customer_order_item (
	id int8 NOT NULL DEFAULT nextval('customer_order_item_seq'::regclass),
	quantity int4 NOT NULL,
	unit_price numeric(38, 2) NOT NULL,
	customer_order_id int8 NOT NULL,
	product_id int8 NOT NULL,
	CONSTRAINT customer_order_item_pkey PRIMARY KEY (id),
	CONSTRAINT fk_customerorderitem_customerorder FOREIGN KEY (customer_order_id) REFERENCES public.customer_order(id),
	CONSTRAINT fk_customerorderitem_product FOREIGN KEY (product_id) REFERENCES public.product(id)
);
