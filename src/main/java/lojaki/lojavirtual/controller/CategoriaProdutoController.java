package lojaki.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			throw new ExceptionLojaki("A empre seve ser informada!");
		}
		
		if (categoriaProduto.getId() == null
				&& categoriaProdutoRepository.existeCategoria(categoriaProduto.getNomeDesc().toUpperCase())) {
			throw new ExceptionLojaki("Categoria já existe com o nome informado!");
		}
		

		CategoriaProduto categoriaSalva = categoriaProdutoRepository.save(categoriaProduto);

		CategoriaProdutoDTO categoriaProdutoDTO = new CategoriaProdutoDTO();

		categoriaProdutoDTO.setId(categoriaSalva.getId());
		categoriaProdutoDTO.setNomeDesc(categoriaSalva.getNomeDesc());
		categoriaProdutoDTO.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		
		return new ResponseEntity<CategoriaProdutoDTO>(categoriaProdutoDTO, HttpStatus.OK);
	}
	
	@PostMapping(value = "**/deleteCategoria")
	public ResponseEntity<String> deleteCategoria(@RequestBody CategoriaProduto categoriaProduto) {
		if (!categoriaProdutoRepository.findById(categoriaProduto.getId()).isPresent()) {
			return new ResponseEntity<>("Categoria já foi removida!", HttpStatus.OK);
		}

		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		return new ResponseEntity<>("Categoria removida com sucesso!", HttpStatus.OK);
	}
	

}
