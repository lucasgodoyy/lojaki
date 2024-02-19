package lojaki.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lojaki.lojavirtual.model.CupomDesconto;


@Repository
public interface CupDescontoRepository extends JpaRepository<CupomDesconto, Long> {

	@Query(value = "select c from CupomDesconto c where c.empresa.id = ?1")
	public List<CupomDesconto> cupDescontoPorEmpresa (Long idEmpresa);
	
}
