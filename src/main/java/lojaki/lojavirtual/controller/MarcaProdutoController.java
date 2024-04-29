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
import lojaki.lojavirtual.model.MarcaProduto;
import lojaki.lojavirtual.repository.MarcaRepository;

@RestController
public class MarcaProdutoController {

	@Autowired
	private MarcaRepository marcaRepository;
	
	
	@PostMapping(value = "**/salvarMarca")
	public ResponseEntity<MarcaProduto> salvarMarca(@RequestBody @Valid MarcaProduto marcaProduto) throws ExceptionLojaki {
		
		if (marcaProduto.getId() == null) {
		
			List<MarcaProduto> marcas = marcaRepository.buscarMarcaProdutoPorNomeDescricao(marcaProduto.getNomeDesc().toUpperCase(), marcaProduto.getEmpresa().getId());
			
			if (!marcas.isEmpty()) {
				throw new ExceptionLojaki("Já existe marca do produto com a descrição: " + marcaProduto.getNomeDesc());
		
			}
		}
		MarcaProduto marcaSalva = marcaRepository.saveAndFlush(marcaProduto);
		
		return new ResponseEntity<>(marcaSalva, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "**/deleteMarca")
	public ResponseEntity<String> deleteMarca(@RequestBody MarcaProduto marcaProduto) {
		
		
			if (!marcaRepository.findById(marcaProduto.getId()).isPresent()) {	
			return new ResponseEntity<String>("Marca do produto com id: "+ marcaProduto.getId() + " não existe ou já foi removida", HttpStatus.OK);
			
		
		}else {marcaRepository.deleteById(marcaProduto.getId());
		
		return new ResponseEntity<String>("Marca do produto com id: " + marcaProduto.getId() +  " removida com sucesso!", HttpStatus.OK);
		}
		
	}
	
	@DeleteMapping(value = "**/deleteMarcaPorId/{id}")
	public ResponseEntity<String> deleteMarcaPorId(@PathVariable("id") Long id) {
		
		marcaRepository.deleteById(id);
		
		return new ResponseEntity<>("Marca do produto removida com sucesso!", HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/obterMarcaProdutoPorId/{id}")
	public ResponseEntity<MarcaProduto> pesquisarMarcaPorId(@PathVariable("id") Long id) throws ExceptionLojaki {
		
		MarcaProduto marcaProduto = marcaRepository.findById(id).orElse(null);
		
		if (marcaProduto == null) {
			throw new ExceptionLojaki("Não encontrou marca de produto com código: " + id);
		}
		
		return new ResponseEntity<MarcaProduto>(marcaProduto, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/listaMarcaProdutoPorEmpresa/{empresaId}")
	public ResponseEntity<List<MarcaProduto>> pesquisarMarcaPorEmpresa(@PathVariable("empresaId") Long empresaId) throws ExceptionLojaki {
		
		List<MarcaProduto> marcaProduto = marcaRepository.findByEmpresa(empresaId);
		
		if (marcaProduto == null) {
			throw new ExceptionLojaki("Não há dados para mostrar, null ");
		}
		
		if (marcaProduto.isEmpty()) {
			throw new ExceptionLojaki("Não há dados para mostrar ");
		}
		
		return new ResponseEntity<List<MarcaProduto>>(marcaProduto, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterMarcaProdutoPorNomeDescricao/{nomeDesc}/{empresa}")
	public ResponseEntity<List<MarcaProduto>> pesquisarPorNomeDescricao(@PathVariable("nomeDesc") String nomeDesc, @PathVariable("empresa") Long empresa) {
	
		List<MarcaProduto> marcas = marcaRepository.buscarMarcaProdutoPorNomeDescricao(nomeDesc.toUpperCase().trim(), empresa);
		
		return new ResponseEntity<List<MarcaProduto>>(marcas, HttpStatus.OK);
	}
	
}
