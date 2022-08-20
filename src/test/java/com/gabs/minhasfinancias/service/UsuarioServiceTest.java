package com.gabs.minhasfinancias.service;

import com.gabs.minhasfinancias.exception.ErroAutenticacaoException;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.repositories.UsuarioRepository;
import com.gabs.minhasfinancias.services.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @MockBean
    UsuarioRepository usuarioRepositoryMock;

    @SpyBean
    UsuarioServiceImpl usuarioService;

    @Test
    public void deveSalvarUmUsuario() {
        // cenário
        Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario
                .builder()
                .id(1l)
                .nome("nome")
                .email("usuario@email.com")
                .senha("senha")
                .build();

        Mockito.when(usuarioRepositoryMock.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        // ação
        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

        // verificação
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        // cenário
        String email = "usuario@email.com";
        Usuario usuario = Usuario.builder().email(email).build();

        Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);

        // ação e verificação
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
            usuarioService.salvarUsuario(usuario);
        });

        Mockito.verify(usuarioRepositoryMock, Mockito.never()).save(usuario);
    }

    @Test
    public void deveValidarEmail() {
        // cenário
        Mockito.when(usuarioRepositoryMock.existsByEmail(Mockito.anyString())).thenReturn(false);

        // ação e verificação
        Assertions.assertThatCode(() -> {
            usuarioService.validarEmail("email@email.com");
        }).doesNotThrowAnyException();

    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        // cenário
        Mockito.when(usuarioRepositoryMock.existsByEmail(Mockito.anyString())).thenReturn(true);

        // ação e verificação
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
            usuarioService.validarEmail("usuario@email.com");
        });

    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        // cenário
        String email = "usuario@email.com", senha = "senha";
        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

        Mockito.when(usuarioRepositoryMock.findByEmail(email)).thenReturn(Optional.of(usuario));

        // ação
        Usuario resultado = usuarioService.autenticar(email, senha);

        // verificação
        Assertions.assertThat(resultado).isNotNull();

        Assertions.assertThatCode(() -> {
            usuarioService.autenticar(email, senha);
        }).doesNotThrowAnyException();

    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){
        // cenário
        Mockito.when(usuarioRepositoryMock.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // ação e verificação
        org.junit.jupiter.api.Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
            usuarioService.autenticar("email@email.com", "senha");
        });

        // capturar a exception
        Throwable exception = Assertions.catchThrowable(() -> {
            usuarioService.autenticar("email@email.com", "senha");
        });

        // verificação se o erro aconteceu na verificação no teste de email
        Assertions.assertThat(exception)
                .isInstanceOf(ErroAutenticacaoException.class)
                .hasMessage("Usuário não encontrado para o email informado");
    }

    @Test
    public void deveLancarErroQuandoAsSenhasNaoForemIguais() {
        // cenário
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("usuario@email.com").senha(senha).build();

        Mockito.when(usuarioRepositoryMock.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        // ação e verificação
        org.junit.jupiter.api.Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
            usuarioService.autenticar("usuario@email.com", "senhaerrada");
        });

        // capturar a exception
        Throwable exception = Assertions.catchThrowable(() -> {
            usuarioService.autenticar("usuario@email.com", "senhaerrada");
        });

        // verificar se o erro aconteceu na verificação da senha
        Assertions.assertThat(exception)
                .isInstanceOf(ErroAutenticacaoException.class)
                .hasMessage("Senha inválida");

    }

}
