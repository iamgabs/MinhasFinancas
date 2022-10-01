package com.gabs.minhasfinancias.service;

import com.gabs.minhasfinancias.annotations.Description;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Lancamento;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.model.enums.StatusLancamento;
import com.gabs.minhasfinancias.repositories.LancamentoRepository;
import com.gabs.minhasfinancias.repositories.LancamentoRepositoryTest;
import com.gabs.minhasfinancias.services.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamentoParaSerSalvo = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoParaSerSalvo);

        Lancamento lancamentoSalvo =  LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.when(repository.save(lancamentoParaSerSalvo)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoParaSerSalvo);

        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamentoParaSerSalvo = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoParaSerSalvo);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> service.salvar(lancamentoParaSerSalvo));

        Mockito.verify(repository, Mockito.never()).save(lancamentoParaSerSalvo);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, ()-> service.atualizar(lancamentoSalvo));

        Mockito.verify(repository, Mockito.never()).save(lancamentoSalvo);
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        service.deletar(lancamento);

        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, ()-> service.deletar(lancamento));

        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = List.of(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultadoDaBusca = service.buscar(lancamento);

        Assertions.assertThat(resultadoDaBusca)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);
    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento statusLancamento = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, statusLancamento);

        Assertions.assertThat(lancamento.getStatus()).isEqualTo(statusLancamento);

        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorId(){
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.buscarPorId(id);

        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioAoTentarObterUmLancamentoPorIdQueNaoExiste(){
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.buscarPorId(id);

        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento(){
        Lancamento lancamento = new Lancamento();

        @Description("deve lancar uma exception pois a descricao está incorreta")
        Throwable erroDescricao = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroDescricao).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida");

        lancamento.setDescricao("descricao");

        @Description("deve lancar uma exception pois o mês está incorreto")
        Throwable erroMesNaoInformado = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroMesNaoInformado).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");

        lancamento.setMes(13);

        @Description("deve lancar uma exception pois o mês está incorreto")
        Throwable erroMesInvalido = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroMesInvalido).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");

        lancamento.setMes(7);

        @Description("deve lancar uma exception pois o ano não foi informado")
        Throwable erroAnoNaoInformado = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroAnoNaoInformado).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido");

        lancamento.setAno(202);

        @Description("deve lancar uma exception pois o ano informado não atende ao formato")
        Throwable erroAnoNaoTeMQuatroDigitos = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroAnoNaoTeMQuatroDigitos).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido");

        lancamento.setAno(2022);

        @Description("deve lancar uma exception pois o lançamento nao possui usuario")
        Throwable erroSemUsuario = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroSemUsuario).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");

        lancamento.setUsuario(new Usuario());

        @Description("deve lancar uma exception pois o lançamento nao possui usuario cadastrado")
        Throwable erroSemUsuarioCadastrado = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroSemUsuarioCadastrado).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");

        lancamento.getUsuario().setId(1L);

        @Description("deve lancar uma exception pois o lançamento nao possui valor")
        Throwable erroSemValorInformado = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroSemValorInformado).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido");

        lancamento.setValor(BigDecimal.ZERO);

        @Description("deve lancar uma exception pois o lançamento nao possui valor válido")
        Throwable erroSemValorValido = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroSemValorValido).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido");

        lancamento.setValor(BigDecimal.valueOf(1));

        @Description("deve lancar uma exception pois o lançamento nao possui um tipo")
        Throwable erroTipoNaoInformado = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erroTipoNaoInformado).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento");
    }

}
