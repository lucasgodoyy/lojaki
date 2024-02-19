package lojaki.lojavirtual.controller;

import java.util.List;

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
import lojaki.lojavirtual.model.NotaFiscalCompra;
import lojaki.lojavirtual.model.NotaFiscalVenda;
import lojaki.lojavirtual.repository.NotaFiscalCompraRepository;
import lojaki.lojavirtual.repository.NotaFiscalVendaRepository;

@RestController
public class NotaFiscalCompraController {

	
	@Autowired
	private NotaFiscalCompraRepository notaFiscalCompraRepository;
	
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@ResponseBody 
	@PostMapping(value = "**/salvarNotaFiscalCompra")
	public ResponseEntity<NotaFiscalCompra> salvarNotaFiscalCompra(@RequestBody @Valid NotaFiscalCompra notaFiscalCompra) throws ExceptionLojaki { /*Recebe o JSON e converte pra Objeto*/
		
		if (notaFiscalCompra.getId() == null) {
		  
			if (notaFiscalCompra.getDescricaoObservacao() != null) {
				boolean existe = notaFiscalCompraRepository.existeNotaComDescricao(notaFiscalCompra.getDescricaoObservacao().toUpperCase().trim());
				   
				if(existe) {
				   throw new ExceptionLojaki("Já existe Nota de compra com essa mesma descrição : " + notaFiscalCompra.getDescricaoObservacao());
			   }
			}
		}
		
		if (notaFiscalCompra.getPessoa() == null || notaFiscalCompra.getPessoa().getId() <= 0) {
			throw new ExceptionLojaki("A Pessoa Juridica da nota fiscal deve ser informada.");
		}
		
		if (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() <= 0) {
			throw new ExceptionLojaki("A empresa responsável deve ser infromada.");
		}
		
		if (notaFiscalCompra.getContaPagar() == null || notaFiscalCompra.getContaPagar().getId() <= 0) {
			throw new ExceptionLojaki("A cponta a pagar da nota deve ser informada.");
		}
		
		
		NotaFiscalCompra notaFiscalCompraSalva = notaFiscalCompraRepository.save(notaFiscalCompra);
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompraSalva, HttpStatus.OK);
	}
	
	
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteNotaFiscalCompraPorId/{id}")
	public ResponseEntity<?> deleteNotaFiscalCompraPorId(@PathVariable("id") Long id) { 
		
		
		notaFiscalCompraRepository.deleteItemNotaFiscalCompra(id);/*Deleta os filhos*/
		
		notaFiscalCompraRepository.deleteById(id); /*Deleta o pai*/
		
		return new ResponseEntity("Nota Fiscal Compra Removida",HttpStatus.OK);
	}
	

	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompra/{id}")
	public ResponseEntity<NotaFiscalCompra> obterNotaFiscalCompra(@PathVariable("id") Long id) throws ExceptionLojaki { 
		
		NotaFiscalCompra notaFiscalCompra = notaFiscalCompraRepository.findById(id).orElse(null);
		
		if (notaFiscalCompra == null) {
			throw new ExceptionLojaki("Não encontrou Nota Fiscal com código: " + id);
		}
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompra, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompraDaVenda/{idVenda}")
	public ResponseEntity<List<NotaFiscalVenda>> obterNotaFiscalCompraDaVenda(@PathVariable("idVenda") Long idVenda) throws ExceptionLojaki { 
		
		List<NotaFiscalVenda>  notaFiscalCompra = notaFiscalVendaRepository.buscaNotaPorVenda(idVenda);
		
		
		if (notaFiscalCompra == null) {
			throw new ExceptionLojaki("Não encontrou Nota Fiscal de venda com código da venda: " + idVenda);
		}
		
		
		return new ResponseEntity<List<NotaFiscalVenda>>(notaFiscalCompra,HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompraDaVendaUnico/{idvenda}")
	public ResponseEntity<NotaFiscalVenda> obterNotaFiscalCompraDaVendaUnico(@PathVariable("idvenda") Long idvenda) throws ExceptionLojaki { 
		
		NotaFiscalVenda notaFiscalCompra = notaFiscalVendaRepository.buscaNotaPorVendaUnica(idvenda);
		
		if (notaFiscalCompra == null) {
			throw new ExceptionLojaki("Não encontrou Nota Fiscal de venda com código da venda: " + idvenda);
		}
		
		return new ResponseEntity<NotaFiscalVenda>(notaFiscalCompra, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarNotaFiscalPorDesc/{desc}")
	public ResponseEntity<List<NotaFiscalCompra>> buscarNotaFiscalPorDesc(@PathVariable("desc") String desc) { 
		
		List<NotaFiscalCompra>  notaFiscalCompras = notaFiscalCompraRepository.buscaNotaDesc(desc.toUpperCase().trim());
		
		return new ResponseEntity<List<NotaFiscalCompra>>(notaFiscalCompras,HttpStatus.OK);
	}
	
	
}
