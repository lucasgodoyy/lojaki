package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lojaki.lojavirtual.model.PessoaFisica;


@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long>{

	
	@Query(value = "SELECT pf FROM PessoaFisica pf WHERE pf.cpf = ?1")
    PessoaFisica existeCpfCadastrado(String cpf);

    @Query(value = "SELECT pf FROM PessoaFisica pf WHERE pf.cpf = ?1")
    List<PessoaFisica> existeCpfCadastradoList(String cpf);
    
    @Query(value = "SELECT pf FROM PessoaFisica pf WHERE upper(trim(pf.nome)) LIKE %?1%")
    List<PessoaFisica> pesquisaPorNomePF(String nome);
	
}
