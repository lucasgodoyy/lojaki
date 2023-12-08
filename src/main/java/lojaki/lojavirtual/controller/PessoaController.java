package lojaki.lojavirtual.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.service.PessoaUserService;

@RestController
public class PessoaController {

	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;

	
	   @Autowired
	    private PessoaUserService pessoaUserService;
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionLojaki {

		if (pessoaJuridica == null) {
			throw new ExceptionLojaki("Pessoa jurídica não pode ser NULL!");
		}
		if (pessoaJuridica.getId() == null
				&& pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionLojaki("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}

		pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);
		

		return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.OK);

	}

}
