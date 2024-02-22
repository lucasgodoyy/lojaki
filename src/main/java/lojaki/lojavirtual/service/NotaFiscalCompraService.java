package lojaki.lojavirtual.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lojaki.lojavirtual.model.dto.ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO;

@Service
public class NotaFiscalCompraService {

	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	

	
	/** Title: Histórico de compras de produtor para a loja.
	 * Este relatório permite saber os produtos comprados para serem vendido pela loja virtual, todos os produtos tem relação com a 
	 * nota fiscal de compra/venda.
	 * @param obejtoRequisicaoRelatorioProdCompraNotaFiscalDto
	 * @param dataInicio e dataFinal são parametros obrigatórios
	 * @return List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO>*/
	 
	public  List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO> gerarRelatorioProdCompraNota(
			ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO obejtoRequisicaoRelatorioProdCompraNotaFiscalDto) {
		
		List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO> retorno = new ArrayList<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO>();
		
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, "
				+ " p.valor_venda as valorVendaProduto, ntp.quantidade as quantidadeComprada, "
				+ " pj.id as codigoFornecedor, pj.nome as nomeFornecedor,cfc.data_compra as dataCompra "
				+ " from nota_fiscal_compra as cfc "
				+ " inner join nota_item_produto as ntp on  cfc.id = nota_fiscal_compra_id "
				+ " inner join produto as p on p.id = ntp.produto_id "
				+ " inner join pessoa_juridica as pj on pj.id = cfc.pessoa_id where ";
		
		sql += " cfc.data_compra >='"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getDataInicial()+"' and ";
		sql += " cfc.data_compra <= '" + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getDataFinal() +"' ";
		
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoNota().isEmpty()) {
		 sql += " and cfc.id = " + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoNota() + " ";
		}
		
		
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoProduto().isEmpty()) {
			sql += " and p.id = " + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoProduto() + " ";
		}
		
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeProduto().isEmpty()) {
			sql += " upper(p.nome) like upper('%"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeProduto()+"') ";
		}
		
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeFornecedor().isEmpty()) {
			sql += " upper(pj.nome) like upper('%"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeFornecedor()+"') ";
		}
		
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO.class));
		
		return retorno;
	}
	
	
}
