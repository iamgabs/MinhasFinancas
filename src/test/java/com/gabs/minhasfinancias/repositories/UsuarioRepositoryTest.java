package com.gabs.minhasfinancias.repositories;

import com.gabs.minhasfinancias.model.entity.Usuario;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    private static Usuario criarUsuario() {
        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }


    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        // cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        // ação/ execução
        boolean result = usuarioRepository.existsByEmail("usuario@email.com");

        // verificação
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
        // ação
        boolean result = usuarioRepository.existsByEmail("usuario@email.com");

        // verificação
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados() {
        // cenário
        Usuario usuario = criarUsuario();
        // ação
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // verificação
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail() {
        // cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        // ação e verificação
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioAoBuscarUmUsuarioPorEmail() {
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();
    }

}
