ALTER TABLE public.category 
ALTER COLUMN id SET DEFAULT nextval('category_seq');

ALTER TABLE public.product 
ALTER COLUMN id SET DEFAULT nextval('product_seq');