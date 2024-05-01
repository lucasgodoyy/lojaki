package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lojaki.lojavirtual.model.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
	public PessoaJuridica existeCnpjCadastrado(String cnpj);
	
	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.email = ?1")
	public PessoaJuridica existeEmailCadastrado(String email);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
	List<PessoaJuridica> existeCnpjCadastradoList(String cnpj);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.inscricaoEstadual = ?1")
	PessoaJuridica existeInscEstadualCadastrado(String inscEstadual);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.inscricaoEstadual = ?1")
	List<PessoaJuridica> existeInscEstadualCadastradoList(String inscEstadual);

	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE upper(trim(pj.nome)) LIKE %?1%")
    List<PessoaJuridica> pesquisaPorNomePJ(String nome);
	
	@Query(nativeQuery = true, value = "select cast((count(1) / 5) as integer) + 1 as qtdpagina  from  pessoa_juridica")
	public Integer qtdPagina();
	
	@Query(value = "select a from PessoaJuridica a")
	public List<PessoaJuridica> findPorPage(Pageable pageable);
	
	
}
