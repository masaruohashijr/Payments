CREATE OR REPLACE FORCE VIEW "FLEX_DIVIDA_PI"."FATO_EVENTO_TRANCHE" AS 
SELECT a.seq_contrato,
       a.nm_contrato,
       a.val_contrato,
       a.dat_final AS dt_final_contrato,
       a.dat_inicial AS dt_inicial_contrato,
       a.seq_credor as seq_credor_contrato,
       d.nm_credor as nom_credor_contrato,
       a.seq_finalidade_operacao,
       e.nom_finalidade,
       a.num_periodo_apuracao,
       a.seq_moeda_contratada AS seq_moeda_contratada_contrato,
       f.abr_moeda as abr_moeda_contratada_contrato,
       a.seq_moeda_local AS seq_moeda_local_contrato,
       g.abr_moeda AS abr_moeda_local_contrato,
       b.seq_tranche_contrato,
       b.nm_tranche_contrato,
       b.cod_tranche_contrato,
       b.dat_final,
       b.dat_inicial,
       b.seq_inst_financeira,
       h.nom_inst_financeira,
       b.seq_moeda_contratad AS seq_moeda_contratada_tranche,
       i.abr_moeda AS abr_moeda_contratada_tranche,
       b.seq_moeda_local AS seq_moeda_local_tranche,
       j.abr_moeda AS abr_moeda_local_tranche,
       b.num_carencia,
       b.num_carencia_meses,
       b.seq_garantia,
       k.nom_garantia,
       b.seq_sistema_amort,
       l.nom_sistema_amortizacao,
       b.dsc_correcao,
       b.dsc_juros,
       c.dat_ocorrencia,
       c.dat_previsao,
       c.nm_obrigacao AS nom_evento,
       c.tip_evento,
       c.sit_evento,
       c.val_evento
   FROM divida_pi_2021.div_contrato a
   LEFT JOIN divida_pi_2021.div_tranche_contrato b ON a.seq_contrato = b.seq_contrato
   LEFT JOIN 
   (SELECT
            a1.seq_tranche_contrato,
            a1.dat_ocorrencia,
            a1.dat_previsao,
            b1.nm_obrigacao,
            a1.tip_evento,
            a1.sit_evento,
            a1.val_evento
        FROM
            divida_pi_2021.div_evento_tranche a1
            LEFT JOIN divida_pi_2021.div_obrigacao b1 ON a1.seq_obrigacao = b1.seq_obrigacao
        UNION ALL
        SELECT
            c1.seq_tranche_contrato,
            c1.dat_ocorrencia,
            c1.dat_previsao,
            c1.nom_liberacao,
            c1.tip_evento,
            c1.sit_evento,
            c1.val_evento
        FROM
            divida_pi_2021.div_liberacao c1) c ON b.seq_tranche_contrato = c.seq_tranche_contrato
    LEFT JOIN divida_pi_2021.div_credor d ON a.seq_credor = d.seq_credor
    LEFT JOIN divida_pi_2021.div_finalidade_operacao e ON a.seq_finalidade_operacao = e.seq_finalidade_operacao
    LEFT JOIN divida_pi_2021.div_moeda f ON a.seq_moeda_contratada = f.seq_moeda
    LEFT JOIN divida_pi_2021.div_moeda g ON a.seq_moeda_local = g.seq_moeda
    LEFT JOIN divida_pi_2021.div_instituicao_financeira h ON b.seq_inst_financeira = h.seq_inst_financeira
    LEFT JOIN divida_pi_2021.div_moeda i ON b.seq_moeda_contratad = i.seq_moeda
    LEFT JOIN divida_pi_2021.div_moeda j ON b.seq_moeda_local = j.seq_moeda
    LEFT JOIN divida_pi_2021.div_garantia_contra_garantia k ON b.seq_garantia = k.seq_garantia
    LEFT JOIN divida_pi_2021.div_sistema_amortizacao l ON b.seq_garantia = l.seq_sistema_amortizacao   
;
    