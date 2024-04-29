package lojaki.lojavirtual.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
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


	@Query("select c from CategoriaProduto c where upper(trim(nome_descricao)) like %?1% and c.empresa.id = ?2")
	    List<CategoriaProduto> buscarCategoriaDesc2(String nomeDesc, Long empresa);
	 

	@Query(nativeQuery = true, value = "select cast ((count(1) / 5) as integer ) + 1 as qtdpagina from categoria_produto where empresa_id = ?1")
		Integer qtdPagina( Long empresa);
	
	 
	@Query(value = "select a from CategoriaProduto a where a.empresa.id = ?1 ")
	public List<CategoriaProduto> findPorPage(Long idEmpresa, Pageable pageable);
	 

}
