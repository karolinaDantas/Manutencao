package br.com.aweb.manutencao.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.server.ResponseStatusException;

import br.com.aweb.manutencao.model.Solicitacao;
import br.com.aweb.manutencao.repository.SolicitacaoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/manutencao")
public class SolicitacaoController {

    @Autowired
    SolicitacaoRepository solicitacaoRepository;

    // Lista todas as solicitações, ordenadas pela data da solicitação (mais recentes primeiro)
    @GetMapping
    public ModelAndView list() {
        return new ModelAndView("list", Map.of(
                "solicitacoes", solicitacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataSolicitacao"))));
    }

    @GetMapping("/create")
    public ModelAndView create() {
        return new ModelAndView("form", Map.of("solicitacao", new Solicitacao()));
    }

    @PostMapping("/create")
    public String create(@Valid Solicitacao solicitacao, BindingResult result) {
        if (result.hasErrors())
            return "form";
        solicitacaoRepository.save(solicitacao);
        return "redirect:/manutencao";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id) {
        var solicitacao = buscarOuLancarNotFound(id);
        // Regra de negócio: solicitação concluída não pode ser editada, mesmo via URL direta
        if (solicitacao.isConcluida())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já concluída não pode ser editada");
        return new ModelAndView("form", Map.of("solicitacao", solicitacao));
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid Solicitacao solicitacao, BindingResult result) {
        if (result.hasErrors())
            return "form";
        var existente = buscarOuLancarNotFound(solicitacao.getId());
        if (existente.isConcluida())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já concluída não pode ser editada");
        // preserva a data original da solicitação, que não é editável no formulário
        solicitacao.setDataSolicitacao(existente.getDataSolicitacao());
        solicitacaoRepository.save(solicitacao);
        return "redirect:/manutencao";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {
        var solicitacao = buscarOuLancarNotFound(id);
        return new ModelAndView("delete", Map.of("solicitacao", solicitacao));
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        var solicitacao = buscarOuLancarNotFound(id);
        solicitacaoRepository.delete(solicitacao);
        return "redirect:/manutencao";
    }

    @PostMapping("/finish/{id}")
    public String finish(@PathVariable Long id) {
        var solicitacao = buscarOuLancarNotFound(id);
        // Regra de negócio: não permite concluir novamente uma solicitação já concluída,
        // mesmo que a requisição seja feita diretamente pela URL
        if (solicitacao.isConcluida())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já foi concluída anteriormente");
        solicitacao.setDataEncerramento(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);
        return "redirect:/manutencao";
    }

    private Solicitacao buscarOuLancarNotFound(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
