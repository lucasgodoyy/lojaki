package lojaki.lojavirtual.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lojaki.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;

@Service
public class VendaService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_Virt_Repository;

	
	public void exclusaoTotalVendaBanco2(Long idVenda) {
		String sql = "begin; update vd_cp_loja_virt set excluido = true where id = " + idVenda +"; commit;";
		jdbcTemplate.execute(sql);;
	}

	
	
	public void exclusaoTotalVendaBanco(Long idVenda) {

		String value = " begin;"
				+ " UPDATE nota_fiscal_venda set venda_compra_loja_virt_id = null where venda_compra_loja_virt_id = "
				+ idVenda + "; " + " delete from nota_fiscal_venda where venda_compra_loja_virt_id = " + idVenda + "; "
				+ " delete from item_venda_loja where venda_compra_loja_virtual_id = " + idVenda + "; "
				+ " delete from status_rastreio where venda_compra_loja_virt_id = " + idVenda + "; "
				+ " delete from vd_cp_loja_virt where id = " + idVenda + "; " 
				+ " commit; ";

		jdbcTemplate.execute(value);
	}
	
	public void ativaRegistroVendaBanco(Long idVenda) {
		String sql = "begin; update vd_cp_loja_virt set excluido = false where id = " + idVenda +"; commit;";
		jdbcTemplate.execute(sql);;
		
	}
	

}
