package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import lojaki.lojavirtual.model.MarcaProduto;

@Transactional
public interface MarcaRepository extends JpaRepository<MarcaProduto, Long> {
	
	@Query("select mp from MarcaProduto mp where upper(trim(mp.nomeDesc)) like %?1% and mp.empresa.id = ?2")
	List<MarcaProduto> buscarMarcaProdutoPorNomeDescricao(String nomeDesc, Long empresa);
	
	
	@Query(value = "select a from MarcaProduto a where a.empresa.id = ?1")
	List<MarcaProduto> findByEmpresa(Long empresaId);


	@Query(nativeQuery = true, value = "select cast ((count(1) / 5) as integer ) + 1 as qtdpagina from marca_produto where empresa_id = ?1")
	Integer buscarQtdPaginas(Long empresa);

	@Query(value = "select a from MarcaProduto a where a.empresa.id = ?1 ")
	List<MarcaProduto> findPorPage(Long idEmpresa, Pageable pageable);

	
	 


}