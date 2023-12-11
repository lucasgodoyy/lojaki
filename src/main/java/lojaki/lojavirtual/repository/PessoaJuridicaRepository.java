package lojaki.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long>{

	
	
	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
    public PessoaJuridica existeCnpjCadastrado(String cnpj);

	
	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.inscricaoEstadual = ?1")
	public PessoaJuridica existeInscEstadualCadastrado(String inscEstadual);

	@Query(value = "SELECT pf FROM PessoaFisica pf WHERE pf.cpf = ?1")
	public PessoaFisica existeCpfCadastradoList(String cpf);
	
}
