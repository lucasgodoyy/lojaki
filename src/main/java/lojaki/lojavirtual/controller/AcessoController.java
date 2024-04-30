package lojaki.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import lojaki.lojavirtual.model.Acesso;
import lojaki.lojavirtual.model.MarcaProduto;
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
	public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) throws ExceptionLojaki {

		if (acesso.getId() == null) {

			List<Acesso> acessos = acessoRepository.buscarAcessoDesc(acesso.getDescricao().toUpperCase());

			if (!acessos.isEmpty()) {
				throw new ExceptionLojaki("Já existe acesso com a descrição: " + acesso.getDescricao());
			}
		}

		Acesso acessoSalvo = acessoService.save(acesso);

		return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);

	}

	@ResponseBody
	@PostMapping(value = "**/deleteAcesso")
	public ResponseEntity<String> deleteAcesso(@RequestBody Acesso acesso) {

		if (!acessoRepository.findById(acesso.getId()).isPresent()){	
			return new ResponseEntity<String>("Marca do produto com id: "+ acesso.getId() 
			+ " não existe ou já foi removida", HttpStatus.OK);
		}
			
		acessoRepository.deleteById(acesso.getId());
		
		return new ResponseEntity<String>("Marca do produto com id: " + acesso.getId() +  " removida com sucesso!", HttpStatus.OK);
		
	}
	

	@ResponseBody
	@DeleteMapping(value = "**/deleteAcessoPorId/{id}")
	public ResponseEntity<String> deleteAcessoPorId(@PathVariable("id") Long id) {
		
		acessoRepository.deleteById(id);
		
		return new ResponseEntity<>("Acesso removido com sucesso!", HttpStatus.OK);
	}

	
	
	@ResponseBody
	@GetMapping(value = "**/obterAcessoPorId/{id}")
	public ResponseEntity<Acesso> pesquisarPorId(@PathVariable("id") Long id) throws ExceptionLojaki {
		Acesso acesso = acessoRepository.findById(id).orElse(null);

		if (acesso == null) {
			throw new ExceptionLojaki("Não foi possível encontrar acesso com id " + id);
		}

		return new ResponseEntity<Acesso>(acesso, HttpStatus.OK);

	}

	@GetMapping(value = "**/obterAcessoPorDescricao/{desc}/{empresa}")
	public ResponseEntity<List<Acesso>> pesquisarPorDescricao(@PathVariable("desc") String desc,
			@PathVariable("empresa") Long empresa) {
		
		List<Acesso> acessos = acessoRepository.buscarAcessoDescEmpresa(desc.toUpperCase(), empresa);
		return new ResponseEntity<List<Acesso>>(acessos, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/listaAcessoByPagina/{pagina}/{empresa}")
	public ResponseEntity<List<Acesso>> listarMarca(@PathVariable("pagina") Integer pagina, 
			@PathVariable("empresa") Long empresa) {
		
		Pageable pageable = PageRequest.of(pagina, 5, Sort.by("descricao"));
		
		List<Acesso> acesso = acessoRepository.findPorPage(empresa, pageable);
		
		return new ResponseEntity<List<Acesso>>(acesso, HttpStatus.OK);
	}

	
	@ResponseBody
	@GetMapping(value = "**/qtdPaginaAcesso/{empresa}")
	public ResponseEntity<Integer> qtdPaginaAcesso(@PathVariable("empresa") Long empresa) {
	
		Integer acesso = acessoRepository.qtdPagina(empresa);
		
		return new ResponseEntity<Integer>(acesso, HttpStatus.OK);
	}
	
	
	
}
