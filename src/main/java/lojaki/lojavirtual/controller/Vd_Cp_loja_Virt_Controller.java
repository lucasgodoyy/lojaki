package lojaki.lojavirtual.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.Endereco;
import lojaki.lojavirtual.model.ItemVendaLoja;
import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.StatusRastreio;
import lojaki.lojavirtual.model.VendaCompraLojaVirtual;
import lojaki.lojavirtual.model.dto.ItemVendaDTO;
import lojaki.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import lojaki.lojavirtual.repository.EnderecoRepository;
import lojaki.lojavirtual.repository.NotaFiscalVendaRepository;
import lojaki.lojavirtual.repository.StatusRastreioRepository;
import lojaki.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;
import lojaki.lojavirtual.service.VendaService;

@RestController
public class Vd_Cp_loja_Virt_Controller {

	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_Virt_Repository;

	
	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private VendaService vendaService; 
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@Autowired
	private PessoaController pessoaController;
	
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarVendaLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaLoja(
			@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual)
			throws ExceptionLojaki, UnsupportedEncodingException, MessagingException {

		
		vendaCompraLojaVirtual.getPessoa().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		PessoaFisica pessoaFisica = pessoaController.salvarPf(vendaCompraLojaVirtual.getPessoa()).getBody();
		vendaCompraLojaVirtual.setPessoa(pessoaFisica);
		
		
		vendaCompraLojaVirtual.getEnderecoCobranca().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoCobranca().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoCobranca = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoCobranca());
		vendaCompraLojaVirtual.setEnderecoCobranca(enderecoCobranca);
		
		vendaCompraLojaVirtual.getEnderecoEntrega().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoEntrega().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoEntrega = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoEntrega());
		vendaCompraLojaVirtual.setEnderecoEntrega(enderecoEntrega);
		
		
		vendaCompraLojaVirtual.getNotaFiscalVenda().setEmpresa(vendaCompraLojaVirtual.getEmpresa());		
		
		
		for (int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		}
		
		
		
		/* Salva primeiro a venda e todo os dados */
		vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.saveAndFlush(vendaCompraLojaVirtual);
		
		StatusRastreio statusRastreio = new StatusRastreio();
		statusRastreio.setCentroDistribuicao("Loja local");
		statusRastreio.setCidade("Loja local");
		statusRastreio.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		statusRastreio.setEstado("rs");
		statusRastreio.setStatus("inicio");
		statusRastreio.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		statusRastreioRepository.save(statusRastreio);
		
		/* Associa a venda gravada no banco com a nota fiscal */
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		/* Persiste novamente as nota fiscal novamente pra ficar amarrada na venda */
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());
		
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());
		compraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());
		compraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(vendaCompraLojaVirtual.getValorFrete());
		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());
		
		
		for (ItemVendaLoja item : vendaCompraLojaVirtual.getItemVendaLojas()) {

			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());

			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}
		
		
		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
		
	}
	

	@ResponseBody
	@GetMapping(value = "**/consultaVendaId/{id}")
	public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda) {

		VendaCompraLojaVirtual compraLojaVirtual = vd_Cp_Loja_Virt_Repository.findById(idVenda).orElse(new VendaCompraLojaVirtual());
		

		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();

		compraLojaVirtualDTO.setValorTotal(compraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(compraLojaVirtual.getPessoa());

		compraLojaVirtualDTO.setEntrega(compraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(compraLojaVirtual.getEnderecoCobranca());

		compraLojaVirtualDTO.setValorDesc(compraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(compraLojaVirtual.getValorFrete());
		compraLojaVirtualDTO.setId(compraLojaVirtual.getId());

		
		for (ItemVendaLoja item : compraLojaVirtual.getItemVendaLojas()) {

			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());

			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}
		
		
		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda excluida com sucesso.",HttpStatus.OK);
		
	}
	
	
	
	
}
