package com.gabs.minhasfinancias.services.impl;

import com.gabs.minhasfinancias.annotations.Description;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Lancamento;
import com.gabs.minhasfinancias.model.enums.StatusLancamento;
import com.gabs.minhasfinancias.model.enums.TipoLancamento;
import com.gabs.minhasfinancias.repositories.LancamentoRepository;
import com.gabs.minhasfinancias.services.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private final LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository lancamentoRepository) {
        this.repository = lancamentoRepository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        this.validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE); // setar status de lançamento como pendente
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        this.validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example<Lancamento> example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        this.atualizar(lancamento);
    }

    @Override
    @Description(value = "Esse método serve para validar um lançamento")
    public void validar(Lancamento lancamento) {
        // verificar se a descrição do lançamento é válida
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException("Informe uma Descrição válida");
        }

        // verificar se o mês do lançamento é válido
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraNegocioException("Informe um Mês válido");
        }

        // verificar se o ano do lançamento é válido
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioException("Informe um Ano válido");
        }

        // verificar se o lançamento possui um usuário válido
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            throw new RegraNegocioException("Informe um usuário");
        }

        // verificar se o valor do lançamento é válido
        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioException("Informe um valor válido");
        }

        // verificar se o tipo do lançamento é válidp
        if(lancamento.getTipo() == null){
            throw new RegraNegocioException("Informe um tipo de lançamento");
        }
    }

    @Override
    public Optional<Lancamento> buscarPorId(long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {
        BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
        BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
        if(receitas == null ){
            receitas = BigDecimal.ZERO;
        }
        if(despesas == null) {
            despesas = BigDecimal.ZERO;
        }

        return receitas.subtract(despesas);
    }
}
