package lojaki.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lojaki.lojavirtual.model.VendaCompraLojaVirtual;


@Repository
@Transactional
public interface Vd_Cp_Loja_Virt_Repository extends JpaRepository<VendaCompraLojaVirtual, Long> {

	
	
	
	
}
