package lojaki.lojavirtual.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.CategoriaProduto;
import lojaki.lojavirtual.model.dto.CategoriaProdutoDTO;
import lojaki.lojavirtual.repository.CategoriaProdutoRepository;

@RestController
public class CategoriaProdutoController {

	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository;

	@ResponseBody
	@PostMapping("**/salvarCategoria")
	public ResponseEntity<CategoriaProdutoDTO> salvarCategoria(@RequestBody CategoriaProduto categoriaProduto)
			throws ExceptionLojaki {

		if (categoriaProduto.getEmpresa() == null || categoriaProduto.getEmpresa().getId() == null) {
			throw new ExceptionLojaki("A empresa seve ser informada!");
		}
		
		if (categoriaProduto.getId() == null
				&& categoriaProdutoRepository.existeCategoria(categoriaProduto.getNomeDesc().toUpperCase())) {
			throw new ExceptionLojaki("Categoria já existe com o nome informado!");
		}
		

		CategoriaProduto categoriaSalva = categoriaProdutoRepository.saveAndFlush(categoriaProduto);

		CategoriaProdutoDTO categoriaProdutoDTO = new CategoriaProdutoDTO();

		categoriaProdutoDTO.setId(categoriaSalva.getId());
		categoriaProdutoDTO.setNomeDesc(categoriaSalva.getNomeDesc());
		categoriaProdutoDTO.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		
		return new ResponseEntity<CategoriaProdutoDTO>(categoriaProdutoDTO, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarPorDescCatgoria/{desc}")
	public ResponseEntity<List<CategoriaProduto>> carregarListaCategoria(@PathVariable("desc") String desc) { 
		
		List<CategoriaProduto> acesso = categoriaProdutoRepository.buscarCategoriaDesc(desc.toUpperCase());
		
		return new ResponseEntity<List<CategoriaProduto>>(acesso,HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarPorDescCategoriaEmp/{desc}/{empresa}")
	public ResponseEntity<List<CategoriaProduto>> carregarListaCategoriaEmpresa(@PathVariable("desc") String desc, @PathVariable("empresa") Long empresa) { 
		
		List<CategoriaProduto> acesso = categoriaProdutoRepository.buscarCategoriaDesc2(desc.toUpperCase(), empresa);
		
		return new ResponseEntity<List<CategoriaProduto>>(acesso,HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/listaPorPageCategoriaProduto/{idEmpresa}/{pagina}")
	public ResponseEntity<List<CategoriaProduto>> page(@PathVariable("idEmpresa") Long idEmpresa,
			@PathVariable("pagina") Integer pagina){
		
		Pageable pageable = PageRequest.of(pagina, 5, Sort.by("nomeDesc"));
		
		List<CategoriaProduto> lista = categoriaProdutoRepository.findPorPage(idEmpresa, pageable); 
		
		return new ResponseEntity<List<CategoriaProduto>>(lista, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarPorId/{id}")
	public ResponseEntity<CategoriaProduto> carregarListaCategoria(@PathVariable("id") Long id) {
		
		
		CategoriaProduto categoriaProduto = categoriaProdutoRepository.findById(id).orElse(null);
		
		
		return new ResponseEntity<CategoriaProduto>(categoriaProduto, HttpStatus.OK);
		
		
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/listarCategoria/{empresaId}")
	public ResponseEntity<List<CategoriaProduto>> buscarPorDesc(@PathVariable("empresaId") Long empresaId) { 
		
		List<CategoriaProduto> listaCategoria = categoriaProdutoRepository.buscarCategoriaPorEmpresa(empresaId);
		
		return new ResponseEntity<List<CategoriaProduto>>(listaCategoria,HttpStatus.OK);
	}
	
	
	
	
	@PostMapping(value = "**/deletarCategoria")
	public ResponseEntity<String> deleteCategoria(@RequestBody CategoriaProduto categoriaProduto) throws ExceptionLojaki {
		
		
		if (!categoriaProdutoRepository.findById(categoriaProduto.getId()).isPresent()) {
			return new ResponseEntity<String>("Categoria não existe ou já foi removida", HttpStatus.OK);
		}

		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		return new ResponseEntity<String>("Categoria removida com sucesso", HttpStatus.OK);
	}

	
	@ResponseBody
	@GetMapping(value = "**/qtdPaginaCategoriaProduto/{idEmpresa}")
	public ResponseEntity<Integer> qtdPagina(@PathVariable("idEmpresa") Long idEmpresa) { 
		
		Integer qtdPagina = categoriaProdutoRepository.qtdPagina(idEmpresa);
		
		return new ResponseEntity<Integer>(qtdPagina,HttpStatus.OK);
	}
	
	
	
	
	
}
