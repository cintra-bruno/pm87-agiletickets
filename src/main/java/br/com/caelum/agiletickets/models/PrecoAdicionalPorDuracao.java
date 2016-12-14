package br.com.caelum.agiletickets.models;

import java.math.BigDecimal;

public class PrecoAdicionalPorDuracao extends PrecoAdicionalUltimosIngrecos {
	
	public PrecoAdicionalPorDuracao() {
		percentualUltimosIngressos = 0.50;
		percentualAdicional = 0.20;
	}
	
	public BigDecimal calculaPreco(Sessao sessao,
			Integer totalIngressos, Integer ingressosReservados,
			BigDecimal preco) {
		
		BigDecimal precoTotal = super.calculaPreco(totalIngressos, ingressosReservados, preco);
		
		if(sessao.getDuracaoEmMinutos() > 60){
			precoTotal = precoTotal.add(preco.multiply(BigDecimal.valueOf(0.10)));
		}
		
		return precoTotal;
	}

}
