package com.gabs.minhasfinancias.services.impl;

import com.gabs.minhasfinancias.exception.ErroAutenticacaoException;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.repositories.UsuarioRepository;
import com.gabs.minhasfinancias.services.UsuarioService;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()) {
            throw new ErroAutenticacaoException("Usuário não encontrado para o email informado");
        }
        if(!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacaoException("Senha inválida");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        this.validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override

    public void validarEmail(String email) throws RegraNegocioException {
        if(repository.existsByEmail(email)){
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
        }
    }
}
