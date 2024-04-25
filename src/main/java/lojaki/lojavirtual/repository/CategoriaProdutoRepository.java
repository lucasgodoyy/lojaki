package lojaki.lojavirtual.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lojaki.lojavirtual.model.CategoriaProduto;

@Transactional
@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long>{
	

	@Transactional
	@Query(value = "select count(1) > 0 from categoria_produto where upper(trim(nome_descricao)) = upper(trim(?1))", nativeQuery = true)
    boolean existeCategoria(String nomeDesc);
	
	
	 @Query("select c from CategoriaProduto c where upper(trim(nome_descricao)) like %?1%")
	    List<CategoriaProduto> buscarCategoriaDesc(String nomeDesc);
	 
	 
	 @Query("select c from CategoriaProduto c where c.empresa.id = ?1")
	    List<CategoriaProduto> buscarCategoriaPorEmpresa(Long empresaId);



	 
	 

}
