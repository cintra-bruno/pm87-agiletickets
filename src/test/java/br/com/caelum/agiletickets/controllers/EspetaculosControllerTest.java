package br.com.caelum.agiletickets.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.agiletickets.models.TipoDeEspetaculo;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;

public class EspetaculosControllerTest {

	private @Mock Agenda agenda;
	private @Mock DiretorioDeEstabelecimentos estabelecimentos;
	private @Spy Validator validator = new MockValidator();
	private @Spy Result result = new MockResult();
	
	private EspetaculosController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new EspetaculosController(result, validator, agenda, estabelecimentos);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemNome() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setDescricao("uma descricao");

		controller.adiciona(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");

		controller.adiciona(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test
	public void deveCadastrarEspetaculosComNomeEDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");
		espetaculo.setDescricao("uma descricao");

		controller.adiciona(espetaculo);

		verify(agenda).cadastra(espetaculo);
	}
	
	@Test
	public void deveRetornarNotFoundSeASessaoNaoExiste() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(null);

		controller.sessao(1234l);

		verify(result).notFound();
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarZeroIngressos() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(new Sessao());

		controller.reserva(1234l, 0);

		verifyZeroInteractions(result);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarMaisIngressosQueASessaoPermite() throws Exception {
		Sessao sessao = new Sessao();
		sessao.setTotalIngressos(3);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reserva(1234l, 5);

		verifyZeroInteractions(result);
	}

	@Test
	public void deveReservarSeASessaoTemIngressosSuficientes() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setTipo(TipoDeEspetaculo.TEATRO);

		Sessao sessao = new Sessao();
		sessao.setPreco(new BigDecimal("10.00"));
		sessao.setTotalIngressos(5);
		sessao.setEspetaculo(espetaculo);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reserva(1234l, 3);

		assertThat(sessao.getIngressosDisponiveis(), is(2));
	}

	@Test
	public void deveCadastrarUmaUnicaSessaoQuandoInicioEFimForemIguais() throws Exception {
		
		Espetaculo espetaculo = new Espetaculo();
		
		LocalDate inicio = new LocalDate(2016,12,14);
		LocalDate fim = new LocalDate(2016,12,14);
		LocalTime horario = new LocalTime(21,0);
		Periodicidade periodicidade = Periodicidade.DIARIA;
		
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertNotNull(sessoes);
		Assert.assertEquals(1, sessoes.size());
		
		Sessao sessao = sessoes.get(0);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("14/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
	}
	
	@Test
	public void deveCadastrarTresSessoesSemanaisQuandoInicioEFimTiveremTresSemanasDeDiferenca() throws Exception {
		
		Espetaculo espetaculo = new Espetaculo();
		
		LocalDate inicio = new LocalDate(2016,12,14);
		LocalDate fim = new LocalDate(2016,12,30);
		LocalTime horario = new LocalTime(21,0);
		Periodicidade periodicidade = Periodicidade.SEMANAL;
		
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertNotNull(sessoes);
		Assert.assertEquals(3, sessoes.size());
		
		Sessao sessao = sessoes.get(0);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("14/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
		sessao = sessoes.get(1);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("21/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
		sessao = sessoes.get(2);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("28/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
	}
	
	@Test
	public void deveCadastrarTresSessoesDiariasQuandoInicioEFimTiveremTresDiasDeDiferenca() throws Exception {
		
		Espetaculo espetaculo = new Espetaculo();
		
		LocalDate inicio = new LocalDate(2016,12,30);
		LocalDate fim = new LocalDate(2017,01,01);
		LocalTime horario = new LocalTime(21,0);
		Periodicidade periodicidade = Periodicidade.DIARIA;
		
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertNotNull(sessoes);
		Assert.assertEquals(3, sessoes.size());
		
		Sessao sessao = sessoes.get(0);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("30/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
		sessao = sessoes.get(1);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("31/12/16", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
		sessao = sessoes.get(2);
		
		Assert.assertEquals(BigDecimal.TEN, sessao.getPreco());
		Assert.assertEquals("01/01/17", sessao.getDia());
		Assert.assertEquals("21:00", sessao.getHora());
		Assert.assertEquals(espetaculo, sessao.getEspetaculo());
		
	}
}
