package lojaki.lojavirtual.controller;

import java.util.List;

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
import lojaki.lojavirtual.enums.TipoPessoa;
import lojaki.lojavirtual.model.Endereco;
import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.model.dto.CepDTO;
import lojaki.lojavirtual.model.dto.ConsultaCnpjDTO;
import lojaki.lojavirtual.repository.EnderecoRepository;
import lojaki.lojavirtual.repository.PessoaFisicaRepository;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.service.PessoaUserService;
import lojaki.lojavirtual.service.ServiceContagemAcessoAPI;
import lojaki.lojavirtual.util.ValidaCNPJ;
import lojaki.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {

	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;

	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private PessoaUserService pessoaUserService;

	@Autowired
	private ServiceContagemAcessoAPI serviceContagemAcessoAPI;
	
	@ResponseBody
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica)
			throws ExceptionLojaki {

		if (pessoaJuridica == null) {
			throw new ExceptionLojaki("Pessoa jurídica não pode ser NULL!");
		}
		
		if (pessoaJuridica.getTipoPessoa() == null) {
			throw new ExceptionLojaki("Informe o tipo Jurídico ou Fornecedor da loja");
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
		} else {
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
		
		if (pessoaFisica.getTipoPessoa() == null) {
            pessoaFisica.setTipoPessoa(TipoPessoa.FISICA.name());
        }
		
		if (pessoaFisica.getId() == null
				&& pessoaFisicaRepository.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			throw new ExceptionLojaki("Já existe CPF cadastrado com o número: " + pessoaFisica.getCpf());
		}

		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionLojaki("CPF: " + pessoaFisica.getCpf() + " está inválido!");
		}

		pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);
		return new ResponseEntity<>(pessoaFisica, HttpStatus.OK);
	}

	/*API Externa*/
	@GetMapping("**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {

		return new ResponseEntity<CepDTO>(pessoaUserService.consultarCep(cep), HttpStatus.OK);
	}
	
	/*API Externa*/
	@GetMapping("**/consultaCnpjReceitaWS/{cnpj}")
    public ResponseEntity<ConsultaCnpjDTO> consultaCnpjReceitaWS(@PathVariable("cnpj") String cnpj) {
        return new ResponseEntity<ConsultaCnpjDTO>(pessoaUserService.consultaCnpjReceitaWS(cnpj), HttpStatus.OK);
    }
	
	
	@ResponseBody
	@GetMapping("**/consultaPfNome/{nome}")
    public ResponseEntity<List<PessoaFisica>> consultaPfNome(@PathVariable("nome") String nome) {
       
		List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.pesquisaPorNomePF(nome.trim().toUpperCase());
       
		serviceContagemAcessoAPI.atualizaAcessoEndpointPF();
		
		return new ResponseEntity<>(pessoaFisicaList, HttpStatus.OK);
    }
	
	@ResponseBody
	 @GetMapping("**/consultaPfCpf/{cpf}")
	    public ResponseEntity<List<PessoaFisica>> consultaPfCpf(@PathVariable("cpf") String cpf) {
	        
		List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.existeCpfCadastradoList(cpf);
	        
	        return new ResponseEntity<>(pessoaFisicaList, HttpStatus.OK);
	    }
	
	@ResponseBody
	@GetMapping("**/consultaNomePJ/{nome}")
    public ResponseEntity<List<PessoaJuridica>> consultaNomePJ(@PathVariable("nome") String nome) {
        
		List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.pesquisaPorNomePJ(nome.trim().toUpperCase());
       
		return new ResponseEntity<>(pessoaJuridicaList, HttpStatus.OK);
    }
	
	@ResponseBody
	 @GetMapping("**/consultaCnpjPJ/{cnpj}")
	    public ResponseEntity<List<PessoaJuridica>> consultaCnpjPJ(@PathVariable("cnpj") String cnpj) {
	        
		List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.existeCnpjCadastradoList(cnpj);
	        
		return new ResponseEntity<>(pessoaJuridicaList, HttpStatus.OK);
	    }
	
	
	
}
