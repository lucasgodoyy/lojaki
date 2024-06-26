package lojaki.lojavirtual.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.Acesso;
import lojaki.lojavirtual.model.Endereco;
import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.model.Usuario;
import lojaki.lojavirtual.model.dto.CepDTO;
import lojaki.lojavirtual.model.dto.ConsultaCnpjDTO;
import lojaki.lojavirtual.model.dto.MensagemSenhaDTO;
import lojaki.lojavirtual.repository.EnderecoRepository;
import lojaki.lojavirtual.repository.PessoaFisicaRepository;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.repository.UsuarioRepository;
import lojaki.lojavirtual.service.PessoaUserService;
import lojaki.lojavirtual.service.ServiceContagemAcessoAPI;
import lojaki.lojavirtual.service.ServiceSendEmail;
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

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ServiceSendEmail serviceSendEmail;

	@ResponseBody
	@PostMapping(value = "**/recuperarSenha")
	public ResponseEntity<MensagemSenhaDTO> recuperarAcesso(@RequestBody String login)
			throws UnsupportedEncodingException, MessagingException {

		Usuario usuario = usuarioRepository.findUserByLogin(login);

		if (usuario == null) {
			return new ResponseEntity<MensagemSenhaDTO>(new MensagemSenhaDTO("Usuário não encontrado"), HttpStatus.OK);

		}

		String senha = UUID.randomUUID().toString();

		senha = senha.substring(0, 6);

		String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

		System.out.println("*******-----------****************----------************");
		System.out.println("nova senha " + senha);

		usuarioRepository.updateSenhaUser(senhaCriptografada, login);

		// StringBuilder msgEmail = new StringBuilder();
		// msgEmail.append("<b>Sua senha nova é <b>").append(senha);

		// serviceSendEmail.enviarEmailHtml("Nova senha", senha,
		// usuario.getPessoa().getEmail());

		return new ResponseEntity<MensagemSenhaDTO>(new MensagemSenhaDTO("Senha enviada para o e-mail"), HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "**/possuiAcesso/{username}/{role}")
	public ResponseEntity<Boolean> possuiAcesso(@PathVariable("username") String username,
			@PathVariable("role") String role) throws ExceptionLojaki {

		String sqlRole = "'" + role.replaceAll(",", "','") + "'";

		Boolean possuiAcesso = pessoaUserService.possuiAcesso(username, sqlRole);

		return new ResponseEntity<Boolean>(possuiAcesso, HttpStatus.OK);

	}

	
	/*end-point é microsservicos é um API*/
	@ResponseBody
	@PostMapping(value = "**/salvarEmpresa")
	public ResponseEntity<PessoaJuridica> salvarEmpresa(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionLojaki{
		
		
		if (pessoaJuridica == null) {
			throw new ExceptionLojaki("Pessoa juridica nao pode ser NULL");
		}
		
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionLojaki("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}
		
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeInscEstadualCadastradoList(pessoaJuridica.getInscricaoEstadual()) == null) {
			throw new ExceptionLojaki("Já existe Inscrição estadual cadastrado com o número: " + pessoaJuridica.getInscricaoEstadual());
		}
		
		
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionLojaki("Cnpj : " + pessoaJuridica.getCnpj() + " está inválido.");
		}
		
		pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);
		
		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				
				String cep = pessoaJuridica.getEnderecos().get(p).getCep();
				CepDTO cepDTO = pessoaUserService.consultarCep(cep);
				
				if (cepDTO == null || (cepDTO != null && cepDTO.getCep() == null)) {
					throw new ExceptionLojaki("CEP : " + cep + " está inválido.");
				}
				
				pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
				pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
				pessoaJuridica.getEnderecos().get(p).setEmpresa(pessoaJuridica);
				pessoaJuridica.getEnderecos().get(p).setPessoa(pessoaJuridica);
				
			}
		}else {
			
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				
				Long idCep = pessoaJuridica.getEnderecos().get(p).getId();
				
				if(idCep != null) {
				 Endereco enderecoTemp =  enderecoRepository.findById(idCep).get();
				
				 if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(p).getCep())) {
					
				
					 CepDTO cepDTO = pessoaUserService.consultarCep(pessoaJuridica.getEnderecos().get(p).getCep());
					
					pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
					pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
					pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
					pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
					pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
					pessoaJuridica.getEnderecos().get(p).setEmpresa(pessoaJuridica.getEmpresa());
					pessoaJuridica.getEnderecos().get(p).setPessoa(pessoaJuridica);
	
					
					enderecoRepository.saveAllAndFlush(pessoaJuridica.getEnderecos());
					
				 } 
				}
			}
		}
		
		
		return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.OK);
	}
	
	
	
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarEmpresaNova")
	public ResponseEntity<PessoaJuridica> salvarEmpresaNova(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionLojaki{
		
		
		if (pessoaJuridica == null) {
			throw new ExceptionLojaki("Pessoa juridica nao pode ser NULL");
		}
		
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionLojaki("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeEmailCadastrado(pessoaJuridica.getEmail()) != null) {
			throw new ExceptionLojaki("Este email já foi cadastrado");
		}
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeInscEstadualCadastradoList(pessoaJuridica.getInscricaoEstadual()) == null) {
			throw new ExceptionLojaki("Já existe Inscrição estadual cadastrado com o número: " + pessoaJuridica.getInscricaoEstadual());
		}
		
		
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionLojaki("Cnpj : " + pessoaJuridica.getCnpj() + " está inválido.");
		}
	
		PessoaJuridica	pessoaJur = pessoaUserService.salvarPessoaJuridicaNova(pessoaJuridica);
		
		
		
		return new ResponseEntity<PessoaJuridica>(pessoaJur, HttpStatus.OK);
		
	}
	

	
	
	
	
	
	@PostMapping(value = "**/salvarPf")
	public ResponseEntity<PessoaFisica> salvarPf(@RequestBody @Valid PessoaFisica pessoaFisica) throws ExceptionLojaki {

		if (pessoaFisica == null) {
			throw new ExceptionLojaki("Pessoa física não pode ser NULL!");
		}

		if (pessoaFisica.getId() == null && pessoaFisicaRepository.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			throw new ExceptionLojaki("Já existe CPF cadastrado com o número: " + pessoaFisica.getCpf());
		}

		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionLojaki("CPF: " + pessoaFisica.getCpf() + " está inválido!");
		}

		
		pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);
		
		
		
		
		
		return new ResponseEntity<>(pessoaFisica, HttpStatus.OK);
	}

	/* API Externa */
	@GetMapping("**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {

		return new ResponseEntity<CepDTO>(pessoaUserService.consultarCep(cep), HttpStatus.OK);
	}

	/* API Externa */
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
	
	
	@ResponseBody
	@GetMapping("**/listaEmpresaByPagina/{pagina}")
	public ResponseEntity<List<PessoaJuridica>> listaEmpresaByPagina(@PathVariable("pagina") Integer pagina) {

		Pageable pageable = PageRequest.of(pagina, 5, Sort.by("nomeFantasia"));
		
		List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findPorPage(pageable);

		return new ResponseEntity<List<PessoaJuridica>>(pessoaJuridicaList, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/qtdPaginaEmpresa")
	public ResponseEntity<Integer> qtdPaginaEmpresa() {
	
		Integer pessoaJuridica = pessoaJuridicaRepository.qtdPagina();
		
		return new ResponseEntity<Integer>(pessoaJuridica, HttpStatus.OK);
	}
	

}
