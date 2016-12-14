package br.com.caelum.agiletickets.models;

import java.math.BigDecimal;

public class PrecoAdicionalUltimosIngrecos {

	public BigDecimal calculaPreco(Integer totalIngressos,
			Integer ingressosReservados, BigDecimal preco, double percentualUltimosIngressos, double valorAdicional) {
		
		BigDecimal precoTotal;
		if ((totalIngressos - ingressosReservados)
				/ totalIngressos.doubleValue() <= percentualUltimosIngressos) {
			precoTotal = preco.add(preco.multiply(BigDecimal.valueOf(valorAdicional)));
		} else {
			precoTotal = preco;
		}
		return precoTotal;
	}
}
