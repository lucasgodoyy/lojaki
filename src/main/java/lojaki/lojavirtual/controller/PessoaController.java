package lojaki.lojavirtual.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.Endereco;
import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.model.dto.CepDTO;
import lojaki.lojavirtual.repository.EnderecoRepository;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.service.PessoaUserService;
import lojaki.lojavirtual.util.ValidaCNPJ;
import lojaki.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {

	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private PessoaUserService pessoaUserService;

	@ResponseBody
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica)
			throws ExceptionLojaki {

		if (pessoaJuridica == null) {
			throw new ExceptionLojaki("Pessoa jurídica não pode ser NULL!");
		}
		if (pessoaJuridica.getId() == null
				&& pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionLojaki("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}

		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionLojaki("CNPJ: " + pessoaJuridica.getCnpj() + " está inválido!");
		}

		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {

				CepDTO cepDTO = pessoaUserService.consultarCep(pessoaJuridica.getEnderecos().get(p).getCep());

				pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
				pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
			}
		} 
		else {
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				Endereco enderecoTemp = enderecoRepository.findById(pessoaJuridica.getEnderecos().get(p).getId()).get();
				if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(p).getCep())) {
					
					
					CepDTO cepDTO = pessoaUserService.consultarCep(pessoaJuridica.getEnderecos().get(p).getCep());

					pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
					pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
					pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
					pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
					pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
					
					
				}
			}
		}

		pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);

		return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.OK);

	}

	@PostMapping(value = "**/salvarPf")
	public ResponseEntity<PessoaFisica> salvarPf(@RequestBody @Valid PessoaFisica pessoaFisica) throws ExceptionLojaki {

		if (pessoaFisica == null) {
			throw new ExceptionLojaki("Pessoa física não pode ser NULL!");
		}
		if (pessoaFisica.getId() == null
				&& pessoaJuridicaRepository.existeCpfCadastradoList(pessoaFisica.getCpf()) != null) {
			throw new ExceptionLojaki("Já existe CPF cadastrado com o número: " + pessoaFisica.getCpf());
		}

		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionLojaki("CPF: " + pessoaFisica.getCpf() + " está inválido!");
		}

		pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);
		return new ResponseEntity<>(pessoaFisica, HttpStatus.OK);
	}

	@GetMapping("**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {

		return new ResponseEntity<CepDTO>(pessoaUserService.consultarCep(cep), HttpStatus.OK);
	}

}
