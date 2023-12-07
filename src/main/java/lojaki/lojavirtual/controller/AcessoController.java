package lojaki.lojavirtual.controller;

import java.util.List;

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

import lojaki.lojavirtual.model.Acesso;
import lojaki.lojavirtual.repository.AcessoRepository;
import lojaki.lojavirtual.service.AcessoService;

@RestController
public class AcessoController {

	@Autowired
	private AcessoService acessoService;

	@Autowired
	private AcessoRepository acessoRepository;

	@ResponseBody
	@PostMapping(value = "**/salvarAcesso")
	public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) {

		Acesso acessoSalvo = acessoService.save(acesso);

		return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);

	}

	@ResponseBody
	@PostMapping(value = "**/deleteAcesso")
	public ResponseEntity<?> deleteAcesso(@RequestBody Acesso acesso) {

		acessoRepository.deleteById(acesso.getId());

		return new ResponseEntity<>("Acesso removido com sucesso!", HttpStatus.OK);

	}

	@ResponseBody
	@DeleteMapping(value = "**/deleteAcessoPorId/{id}")
	public ResponseEntity<String> deleteAcessoPorId(@PathVariable("id") Long id) {
		acessoRepository.deleteById(id);
		return new ResponseEntity<>("Acesso removido com sucesso!", HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "**/obterAcessoPorId/{id}")
	public ResponseEntity<Acesso> pesquisarPorId(@PathVariable("id") Long id)   {
		Acesso acesso = acessoRepository.findById(id).get();
		return new ResponseEntity<Acesso>(acesso, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "**/obterAcessoPorDescricao/{desc}")
	public ResponseEntity<List<Acesso>> pesquisarPorDescricao(@PathVariable("desc") String desc) {
		List<Acesso> acessos = acessoRepository.buscarAcessoDesc(desc.toUpperCase());
		return new ResponseEntity<>(acessos, HttpStatus.OK);
	}
	
	
	
}
