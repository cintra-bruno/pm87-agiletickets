package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.PrecoAdicionalPorDuracao;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.agiletickets.models.PrecoAdicionalUltimosIngrecos;
import br.com.caelum.agiletickets.models.TipoDeEspetaculo;

public class CalculadoraDePrecos {

	public static BigDecimal calcula(Sessao sessao, Integer quantidade) {
		BigDecimal precoTotal;
		
		TipoDeEspetaculo tipo = sessao.getEspetaculo().getTipo();
		Integer totalIngressos = sessao.getTotalIngressos();
		Integer ingressosReservados = sessao.getIngressosReservados();
		BigDecimal preco = sessao.getPreco();

		switch (tipo) {
		case CINEMA:
		case SHOW:
			//quando estiver acabando os ingressos... 
			precoTotal = new PrecoAdicionalUltimosIngrecos().calculaPreco(totalIngressos, ingressosReservados,
					preco, 0.05, 0.10);
			break;
			
		case BALLET:
		case ORQUESTRA:
			precoTotal = new PrecoAdicionalPorDuracao().calculaPreco(sessao, totalIngressos,
					ingressosReservados, preco, 0.50, 0.20);
			break;
			
		default:
			precoTotal = preco;
			break;
		}

		return precoTotal.multiply(BigDecimal.valueOf(quantidade));
	}


	

}