-- Criação da SEQUENCE para gerar valores automaticamente para a chave primária
CREATE SEQUENCE product_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

-- Criação da tabela PRODUCT
CREATE TABLE product (
    id INT PRIMARY KEY DEFAULT nextval('product_seq'), -- Chave primária com incremento via sequence
    product_id VARCHAR(50) NOT NULL UNIQUE,               -- Identificador único do produto
    name VARCHAR(100) NOT NULL,                           -- Nome do produto
    price NUMERIC(10, 2) NOT NULL,                        -- Preço do produto
    short_description VARCHAR(255),                       -- Descrição curta do produto
    long_description TEXT,                                -- Descrição longa do produto
    stock INT DEFAULT 0                                   -- Estoque do produto, valor padrão 0
);

-- Garante que a sequência seja usada corretamente no campo id da tabela product
ALTER SEQUENCE product_seq OWNED BY product.id;

-- Criação da SEQUENCE para a chave primária da tabela de categorias
CREATE SEQUENCE category_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

-- Criação da tabela CATEGORY
CREATE TABLE category (
    id INT PRIMARY KEY DEFAULT nextval('category_seq'), -- Chave primária com incremento via sequence
    name VARCHAR(100) NOT NULL                             -- Nome da categoria
);

-- Garante que a sequência seja usada corretamente no campo id da tabela category
ALTER SEQUENCE category_seq OWNED BY category.id;

-- Adiciona a coluna category_id na tabela PRODUCT e define como chave estrangeira
ALTER TABLE product
ADD COLUMN category_id INT,                               -- Chave estrangeira para a categoria
ADD CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES category(id);    -- Relacionamento entre product e category
