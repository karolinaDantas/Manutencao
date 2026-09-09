package br.com.aweb.manutencao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aweb.manutencao.model.Solicitacao;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
}
