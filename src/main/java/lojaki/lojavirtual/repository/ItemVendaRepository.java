package lojaki.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lojaki.lojavirtual.model.ItemVendaLoja;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVendaLoja, Long> {

	
	
	
	
}
