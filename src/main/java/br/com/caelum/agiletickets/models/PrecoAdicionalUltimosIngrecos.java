package br.com.caelum.agiletickets.models;

import java.math.BigDecimal;

public class PrecoAdicionalUltimosIngrecos {
	
	protected double percentualUltimosIngressos;
	protected double percentualAdicional;
	
	public PrecoAdicionalUltimosIngrecos() {
		percentualUltimosIngressos = 0.05;
		percentualAdicional = 0.10;
	}

	public BigDecimal calculaPreco(Integer totalIngressos,
			Integer ingressosReservados, BigDecimal preco) {
		
		BigDecimal precoTotal;
		if ((totalIngressos - ingressosReservados)
				/ totalIngressos.doubleValue() <= percentualUltimosIngressos) {
			precoTotal = preco.add(preco.multiply(BigDecimal.valueOf(percentualAdicional)));
		} else {
			precoTotal = preco;
		}
		return precoTotal;
	}
}
