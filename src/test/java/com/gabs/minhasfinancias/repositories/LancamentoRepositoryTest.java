package com.gabs.minhasfinancias.repositories;

import com.gabs.minhasfinancias.model.entity.Lancamento;
import com.gabs.minhasfinancias.model.enums.StatusLancamento;
import com.gabs.minhasfinancias.model.enums.TipoLancamento;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2022)
                .mes(10)
                .descricao("lancamento")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENDTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    private Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = this.criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }


    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = this.criarLancamento();

        lancamento = lancamentoRepository.save(lancamento);

        Assertions.assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = this.criarEPersistirLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRepository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamentoInexistente).isNull();

    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = this.criarEPersistirLancamento();

        lancamento.setAno(2020);
        lancamento.setDescricao("Teste atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        lancamentoRepository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2020);
        Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste atualizar");
        Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = this.criarEPersistirLancamento();

        Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(lancamento.getId());

        Assertions.assertThat(lancamentoSalvo.isPresent()).isTrue();
    }

}
