package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import lojaki.lojavirtual.model.MarcaProduto;

@Transactional
public interface MarcaRepository extends JpaRepository<MarcaProduto, Long> {
	
	@Query("select mp from MarcaProduto mp where upper(trim(mp.nomeDesc)) like %?1%")
	List<MarcaProduto> buscarMarcaProdutoPorNomeDescricao(String nomeDesc);

}