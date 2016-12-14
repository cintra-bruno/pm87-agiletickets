package br.com.caelum.agiletickets.models;

import java.math.BigDecimal;

public class PrecoAdicionalPorDuracao extends PrecoAdicionalUltimosIngrecos {
	
	public BigDecimal calculaPreco(Sessao sessao,
			Integer totalIngressos, Integer ingressosReservados,
			BigDecimal preco, double percentualUltimosIngressos, double valorAdicional) {
		
		BigDecimal precoTotal = super.calculaPreco(totalIngressos, ingressosReservados, preco, percentualUltimosIngressos, valorAdicional);
		
		if(sessao.getDuracaoEmMinutos() > 60){
			precoTotal = precoTotal.add(preco.multiply(BigDecimal.valueOf(0.10)));
		}
		
		return precoTotal;
	}

}
