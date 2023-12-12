package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lojaki.lojavirtual.model.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
	public PessoaJuridica existeCnpjCadastrado(String cnpj);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
	List<PessoaJuridica> existeCnpjCadastradoList(String cnpj);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.inscricaoEstadual = ?1")
	PessoaJuridica existeInscEstadualCadastrado(String inscEstadual);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.inscricaoEstadual = ?1")
	List<PessoaJuridica> existeInscEstadualCadastradoList(String inscEstadual);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE upper(trim(pj.nome)) LIKE %?1%")
    List<PessoaJuridica> pesquisaPorNomePJ(String nome);
	
}
