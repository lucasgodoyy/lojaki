package lojaki.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lojaki.lojavirtual.model.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long>{

	
	
	@Query(value = "SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj = ?1")
    PessoaJuridica existeCnpjCadastrado(String cnpj);

	
	
}
