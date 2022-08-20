package com.gabs.minhasfinancias.repositories;

import com.gabs.minhasfinancias.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
