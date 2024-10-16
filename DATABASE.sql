-- public.category definition

-- Drop table

-- DROP TABLE public.category;

-- public.category_seq definition

-- DROP SEQUENCE public.category_seq;

CREATE SEQUENCE public.category_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- public.image_seq definition

-- DROP SEQUENCE public.image_seq;

CREATE SEQUENCE public.image_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- public.product_seq definition

-- DROP SEQUENCE public.product_seq;

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


-- public.product definition

-- Drop table

-- DROP TABLE public.product;

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


-- public.image definition

-- Drop table

-- DROP TABLE public.image;

CREATE TABLE public.image (
	id int8 NOT NULL DEFAULT nextval('image_seq'::regclass),
	file_name varchar(255) NOT NULL,
	"name" varchar(100) NOT NULL,
	"order" int4 NOT NULL,
	product_id int8 NOT NULL,
	CONSTRAINT image_pkey PRIMARY KEY (id),
	CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES public.product(id)
);
