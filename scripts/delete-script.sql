delete from divida_pi_2022.div_liberacao where seq_liberacao > 0;
delete from divida_pi_2022.div_evento_tranche where seq_evento_contrato > 0;
delete from divida_pi_2022.div_obrigacao where seq_obrigacao > 0;
delete from divida_pi_2022.div_tranche_contrato where seq_tranche_contrato > 0;
delete from divida_pi_2022.div_contrato where seq_contrato > 0;
delete from divida_pi_2022.div_sistema_amortizacao where seq_sistema_amortizacao > 0;
delete from divida_pi_2022.div_garantia_contra_garantia where seq_garantia > 0;
commit;

--Update para Alterar os tipos de eventos para Quitação de Obrigação
UPDATE divida_pi_2021.div_evento_tranche set tip_evento = 'QUITACAO_OBRIGACAO' where tip_evento != 'LIBERACAO';
commit;