package com.gabs.minhasfinancias.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabs.minhasfinancias.dto.UsuarioDTO;
import com.gabs.minhasfinancias.exception.ErroAutenticacaoException;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.services.LancamentoService;
import com.gabs.minhasfinancias.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;
    static final String email = "usuario@email.com", senha = "1234";


    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    private UsuarioDTO criarUsuarioDTO(){
        return UsuarioDTO.builder()
                .email(email)
                .senha(senha).build();
    }

    @Test
    public void deveAutenticarUmUsuario() throws Exception{
        UsuarioDTO dto = this.criarUsuarioDTO();

        Usuario usuarioAutenticado = Usuario.builder()
                .id(1L)
                .email(email)
                .senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuarioAutenticado);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioAutenticado.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioAutenticado.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioAutenticado.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception{
        UsuarioDTO dto = this.criarUsuarioDTO();

        Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deveCriarUmUsuario() throws Exception{
        UsuarioDTO dto = this.criarUsuarioDTO();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .email(email)
                .senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
        UsuarioDTO dto = this.criarUsuarioDTO();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
