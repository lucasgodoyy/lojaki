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
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.ContaPagar;
import lojaki.lojavirtual.repository.ContaPagarRepository;

@RestController
public class ContaPagarController {

	@Autowired
	private ContaPagarRepository contaPagarRepository;

	@PostMapping(value = "**/salvarContaPagar")
	public ResponseEntity<ContaPagar> salvarContaPagar(@RequestBody @Valid ContaPagar contaPagar)
			throws ExceptionLojaki {

		if (contaPagar.getEmpresa() == null || contaPagar.getEmpresa().getId() <= 0) {
			throw new ExceptionLojaki("Empresa responsável deve ser informada!");
		}

		if (contaPagar.getPessoa() == null || contaPagar.getPessoa().getId() <= 0) {
			throw new ExceptionLojaki("Pessoa responsável deve ser informada!");
		}

		if (contaPagar.getPessoaFornecedor() == null || contaPagar.getPessoaFornecedor().getId() <= 0) {
			throw new ExceptionLojaki("Fornecedor responsável deve ser informado!");
		}

		if (contaPagar.getId() == null) {
			List<ContaPagar> contaPagarList = contaPagarRepository
					.buscarContaPorDescricao(contaPagar.getDescricao().toUpperCase().trim());
			if (!contaPagarList.isEmpty()) {
				throw new ExceptionLojaki(
						"Já existe conta a pagar com a descrição informada! " + contaPagar.getDescricao());
			}
		}

		ContaPagar contaPagarSalva = contaPagarRepository.save(contaPagar);
		return new ResponseEntity<>(contaPagarSalva, HttpStatus.OK);
	}

	@PostMapping(value = "**/deleteContaPagar")
	public ResponseEntity<String> deleteContaPagar(@RequestBody ContaPagar contaPagar) {

		contaPagarRepository.deleteById(contaPagar.getId());

		return new ResponseEntity<>("Conta a pagar removida com sucesso!", HttpStatus.OK);
	}

	@DeleteMapping(value = "**/deleteContaPagarPorId/{id}")
	public ResponseEntity<String> deleteContaPagarPorId(@PathVariable("id") Long id) {

		contaPagarRepository.deleteById(id);

		return new ResponseEntity<String>("Conta a pagar removida com sucesso!", HttpStatus.OK);
	}

	@GetMapping(value = "**/obterContaPagarPorId/{id}")
	public ResponseEntity<ContaPagar> obterContaPagarPorId(@PathVariable("id") Long id) throws ExceptionLojaki {

		ContaPagar contaPagar = contaPagarRepository.findById(id).orElse(null);

		if (contaPagar == null) {
			throw new ExceptionLojaki("Não encontrou marca de produto com código: " + id);
		}

		return new ResponseEntity<ContaPagar>(contaPagar, HttpStatus.OK);
	}

	@GetMapping(value = "**/obterContaPagarPorDescricao/{descricao}")
	public ResponseEntity<List<ContaPagar>> obterContaPagarPorDescricao(@PathVariable("descricao") String descricao) {

		List<ContaPagar> contaPagarList = contaPagarRepository.buscarContaPorDescricao(descricao.toUpperCase());

		return new ResponseEntity<>(contaPagarList, HttpStatus.OK);
	}

}
