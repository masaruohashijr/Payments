ALTER SESSION SET CURRENT_SCHEMA=DIVIDA_PI_2022

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_CONTRATO
BEFORE INSERT ON DIV_CONTRATO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_CONTRATO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_CONTRATO FROM SEQUENCE WHERE seq_name = 'seq_contrato';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_CONTRATO WHERE seq_name = 'seq_contrato';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_TRANCHE
BEFORE INSERT ON DIV_TRANCHE_CONTRATO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_TRANCHE_CONTRATO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_TRANCHE_CONTRATO FROM SEQUENCE WHERE seq_name = 'seq_tranche_contrato';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_TRANCHE_CONTRATO WHERE seq_name = 'seq_tranche_contrato';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_LIBERACAO
BEFORE INSERT ON DIV_LIBERACAO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_LIBERACAO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_LIBERACAO FROM SEQUENCE WHERE seq_name = 'seq_liberacao';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_LIBERACAO WHERE seq_name = 'seq_liberacao';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_OBRIGACAO
BEFORE INSERT ON DIV_OBRIGACAO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_OBRIGACAO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_OBRIGACAO FROM SEQUENCE WHERE seq_name = 'seq_obrigacao';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_OBRIGACAO WHERE seq_name = 'seq_obrigacao';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_EVENTO
BEFORE INSERT ON DIV_EVENTO_TRANCHE
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_EVENTO_CONTRATO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_EVENTO_CONTRATO FROM SEQUENCE WHERE seq_name = 'seq_evento_contrato';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_EVENTO_CONTRATO WHERE seq_name = 'seq_evento_contrato';
	END IF;
END;


CREATE OR REPLACE TRIGGER TRIGGER_INSERT_MOEDA
BEFORE INSERT ON DIV_MOEDA
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_MOEDA IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_MOEDA FROM SEQUENCE WHERE seq_name = 'seq_moeda';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_MOEDA WHERE seq_name = 'seq_moeda';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_CREDOR
BEFORE INSERT ON DIV_CREDOR
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_CREDOR IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_CREDOR FROM SEQUENCE WHERE seq_name = 'seq_credor';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_CREDOR WHERE seq_name = 'seq_credor';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_FINALIDADE
BEFORE INSERT ON DIV_FINALIDADE_OPERACAO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_FINALIDADE_OPERACAO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_FINALIDADE_OPERACAO FROM SEQUENCE WHERE seq_name = 'seq_finalidade_operacao';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_FINALIDADE_OPERACAO WHERE seq_name = 'seq_finalidade_operacao';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_INDEXADOR
BEFORE INSERT ON DIV_INDEXADOR
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_INDEXADOR IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_INDEXADOR FROM SEQUENCE WHERE seq_name = 'seq_indexador';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_INDEXADOR WHERE seq_name = 'seq_indexador';
	END IF;
END;

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_INST_FIN
BEFORE INSERT ON DIV_INSTITUICAO_FINANCEIRA
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_INST_FINANCEIRA IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_INST_FINANCEIRA FROM SEQUENCE WHERE seq_name = 'seq_inst_financeira';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_INST_FINANCEIRA WHERE seq_name = 'seq_inst_financeira';
	END IF;
END;


CREATE OR REPLACE TRIGGER TRIGGER_INSERT_SISTEMA
BEFORE INSERT ON DIV_SISTEMA_AMORTIZACAO
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_SISTEMA_AMORTIZACAO IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_SISTEMA_AMORTIZACAO FROM SEQUENCE WHERE seq_name = 'seq_sistema_amortizacao';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_SISTEMA_AMORTIZACAO WHERE seq_name = 'seq_sistema_amortizacao';
	END IF;
END;

ALTER SESSION set current_schema=DIVIDA_PI_2022

CREATE OR REPLACE TRIGGER TRIGGER_INSERT_GARANTIA
BEFORE INSERT ON DIV_GARANTIA_CONTRA_GARANTIA
FOR EACH ROW
BEGIN
	IF :NEW.SEQ_GARANTIA IS NULL THEN
		SELECT (SEQ_COUNT+1) INTO :NEW.SEQ_GARANTIA FROM SEQUENCE WHERE seq_name = 'seq_garantia';
		UPDATE SEQUENCE SET SEQ_COUNT = :NEW.SEQ_GARANTIA WHERE seq_name = 'seq_garantia';
	END IF;
END;
