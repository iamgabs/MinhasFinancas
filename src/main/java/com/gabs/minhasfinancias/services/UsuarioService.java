package com.gabs.minhasfinancias.services;

import com.gabs.minhasfinancias.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);

    Optional<Usuario> obterPorId(long id);
}
