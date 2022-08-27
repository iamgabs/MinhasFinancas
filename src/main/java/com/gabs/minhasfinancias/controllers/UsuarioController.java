package com.gabs.minhasfinancias.controllers;

import com.gabs.minhasfinancias.dto.UsuarioDTO;
import com.gabs.minhasfinancias.exception.ErroAutenticacaoException;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.services.LancamentoService;
import com.gabs.minhasfinancias.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final LancamentoService lancamentoService;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try{
            Usuario usuarioAutenticado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (ErroAutenticacaoException erroAutenticacaoException) {
            return ResponseEntity.badRequest().body(erroAutenticacaoException.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();

        try {
            Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED); // 200
        } catch (RegraNegocioException regraNegocioException) {
            return ResponseEntity.badRequest().body(regraNegocioException.getMessage());
        }
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = usuarioService.obterPorId(id);

        if(!usuario.isPresent()){
            return new ResponseEntity("Usuário não encontrado para este id", HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
