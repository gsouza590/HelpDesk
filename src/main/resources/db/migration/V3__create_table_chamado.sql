CREATE TABLE chamado (
    id INT PRIMARY KEY,
    cliente_id INT,
    tecnico_id INT,
    data_abertura DATE,
    data_fechamento DATE,
    prioridade INT,
    status INT,
    observacoes TEXT,
    titulo VARCHAR(255)
);