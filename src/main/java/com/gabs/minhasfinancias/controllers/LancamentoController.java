package com.gabs.minhasfinancias.controllers;


import com.gabs.minhasfinancias.dto.AtualizaStatusDTO;
import com.gabs.minhasfinancias.dto.LancamentoDTO;
import com.gabs.minhasfinancias.exception.RegraNegocioException;
import com.gabs.minhasfinancias.model.entity.Lancamento;
import com.gabs.minhasfinancias.model.entity.Usuario;
import com.gabs.minhasfinancias.model.enums.StatusLancamento;
import com.gabs.minhasfinancias.model.enums.TipoLancamento;
import com.gabs.minhasfinancias.services.LancamentoService;
import com.gabs.minhasfinancias.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class LancamentoController {
    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(
                () -> new RegraNegocioException("Usuario não encontrado para o id informado"));

        lancamento.setUsuario(usuario);

        if(dto.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));

        }

        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }

    private LancamentoDTO converter(Lancamento lancamento){
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .ano(lancamento.getAno())
                .mes(lancamento.getMes())
                .valor(lancamento.getValor())
                .tipo(lancamento.getTipo().name())
                .status(lancamento.getStatus().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto)  {
        Lancamento lancamentoEntity = converter(dto);
        lancamentoEntity = lancamentoService.salvar(lancamentoEntity);
        try {
            return new ResponseEntity(lancamentoEntity, HttpStatus.CREATED);
        } catch (RegraNegocioException regraNegocioException) {
            return ResponseEntity.badRequest().body(regraNegocioException.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return lancamentoService.buscarPorId(id).map( entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException regraNegocioException) {
                return ResponseEntity.badRequest().body(regraNegocioException.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
        return lancamentoService.buscarPorId(id).map( entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido!");
            }
            try{
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException regraNegocioException) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o lançamento!");
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return lancamentoService.buscarPorId(id).map( entity -> {
            try {
                lancamentoService.deletar(entity);
                return new ResponseEntity( HttpStatus.NO_CONTENT );
            } catch (RegraNegocioException regraNegocioException) {
                return ResponseEntity.badRequest().body(regraNegocioException.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));

    }

    @GetMapping
    public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
                                 @RequestParam(value = "mes", required = false) Integer mes,
                                 @RequestParam(value = "ano", required = false) Integer ano,
                                 @RequestParam(value = "tipo", required = false) TipoLancamento tipo,
                                 @RequestParam("usuario") Long idUsuario ){

        Lancamento lancamentoFiltro =  new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setTipo(tipo);
        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(usuario.isPresent()) {
            lancamentoFiltro.setUsuario(usuario.get());
        } else {
            return ResponseEntity.badRequest().body("Usuario não encontrado para o id informado!");
        }
        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return ResponseEntity.ok().body(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id){
        return lancamentoService.buscarPorId(id)
                .map( lancamento -> new ResponseEntity(this.converter(lancamento), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

}
