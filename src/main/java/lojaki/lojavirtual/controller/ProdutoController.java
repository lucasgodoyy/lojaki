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
import lojaki.lojavirtual.model.Produto;
import lojaki.lojavirtual.repository.ProdutoRepository;


@RestController
public class ProdutoController {

	@Autowired
	ProdutoRepository produtoRepository;

	@ResponseBody
	@PostMapping(value = "**/salvarProdutos")
	public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid Produto produto) throws ExceptionLojaki {

		
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionLojaki("Empresa responsável deve ser informada!");
		}
		
		if (produto.getId() == null) {
			List<Produto> produtos = produtoRepository.buscarProdutoNome(produto.getNome()
					.toUpperCase(), produto.getEmpresa().getId());
			
			if (!produtos.isEmpty()) {
				throw new ExceptionLojaki("Já existe produto com o nome: " + produto.getNome());
			}
		}


		if (produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <= 0) {
			throw new ExceptionLojaki("Categoria do produto deve ser informada!");
		}
		
		if (produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <= 0) {
			throw new ExceptionLojaki("Marca do produto deve ser informada!");
		}
		
		Produto produtoSalvo = produtoRepository.save(produto);

		return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "**/deleteProduto")
	public ResponseEntity<String> deleteProduto(@RequestBody Produto produto) {
		produtoRepository.deleteById(produto.getId());
		return new ResponseEntity<>("Produto removido com sucesso!", HttpStatus.OK);
	}
	
	
	@DeleteMapping(value = "**/deleteProdutoPorId/{id}")
	public ResponseEntity<String> deleteProdutoPorId(@PathVariable("id") Long id) {
		produtoRepository.deleteById(id);
		return new ResponseEntity<>("Produto removido com sucesso!", HttpStatus.OK);
	}
	
	
	@GetMapping(value = "**/obterProdutoPorId/{id}")
	public ResponseEntity<Produto> pesquisarPorId(@PathVariable("id") Long id) throws ExceptionLojaki {
		
		Produto produto = produtoRepository.findById(id).orElseThrow(() -> new ExceptionLojaki("Produto com o id " + id + " não encontrado!"));
		
		return new ResponseEntity<>(produto, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/obterProdutoPorNome/{nome}")
	public ResponseEntity<List<Produto>> obterProdutoPorNome(@PathVariable("nome") String nome) {
		
		List<Produto> produtos = produtoRepository.buscarProdutoNome(nome.toUpperCase());
		
		return new ResponseEntity<>(produtos, HttpStatus.OK);
	}
	

}
