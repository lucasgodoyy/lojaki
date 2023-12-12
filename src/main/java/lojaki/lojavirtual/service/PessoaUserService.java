package lojaki.lojavirtual.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lojaki.lojavirtual.model.PessoaFisica;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.model.Usuario;
import lojaki.lojavirtual.model.dto.CepDTO;
import lojaki.lojavirtual.model.dto.ConsultaCnpjDTO;
import lojaki.lojavirtual.repository.PessoaFisicaRepository;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoaUserService {

	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ServiceSendEmail serviceSendEmail;

	public PessoaJuridica salvarPessoaJuridica(PessoaJuridica pessoaJuridica) {

		pessoaJuridica = pessoaJuridicaRepository.save(pessoaJuridica);

		Usuario usuarioPj = usuarioRepository.findUserByPessoa(pessoaJuridica.getId(), pessoaJuridica.getEmail());

		if (usuarioPj == null) {
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
			}

			usuarioPj = new Usuario();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPj.setEmpresa(pessoaJuridica);
			usuarioPj.setPessoa(pessoaJuridica);
			usuarioPj.setLogin(pessoaJuridica.getEmail());

			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

			usuarioPj.setSenha(senhaCriptografada);
			usuarioPj = usuarioRepository.save(usuarioPj);

			usuarioRepository.insereAcessoUser(usuarioPj.getId());
			usuarioRepository.insereAcessoUserPj(usuarioPj.getId(), "ROLE_ADMIN");

			
			// Envio e-mail login e senha
			StringBuilder mensagemHtml = new StringBuilder();
			mensagemHtml.append("<b>Segue abaixo seus dados de acesso Pessoa Jurídica para a loja virtual:</b><br/><br/>");
			mensagemHtml.append("<b>Login:</b> " + pessoaJuridica.getEmail() + "<br/>");
			mensagemHtml.append("<b>Senha:</b> ").append(senha).append("<br/><br/>");
			mensagemHtml.append("<b>Obrigado!</b>");
			try {
				serviceSendEmail.enviarEmailHtml("Acesso gerado para Loja Virtual", mensagemHtml.toString(),
						pessoaJuridica.getEmail());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return pessoaJuridica;

	}

	public PessoaFisica salvarPessoaFisica(PessoaFisica pessoaFisica) {

		

		for (int i = 0; i < pessoaFisica.getEnderecos().size(); i++) {
			pessoaFisica.getEnderecos().get(i).setPessoa(pessoaFisica);
			//pessoaFisica.getEnderecos().get(i).setEmpresa(pessoaFisica);
		}

		pessoaFisica = pessoaFisicaRepository.save(pessoaFisica);

		Usuario usuarioPf = usuarioRepository.findUserByPessoa(pessoaFisica.getId(), pessoaFisica.getEmail());

		if (usuarioPf == null) {
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
			}
			usuarioPf = new Usuario();
			usuarioPf.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPf.setEmpresa(pessoaFisica.getEmpresa());
			usuarioPf.setPessoa(pessoaFisica);
			usuarioPf.setLogin(pessoaFisica.getEmail());

			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

			usuarioPf.setSenha(senhaCriptografada);
			usuarioPf = usuarioRepository.save(usuarioPf);

			usuarioRepository.insereAcessoUser(usuarioPf.getId());

			// Envio e-mail login e senha
			StringBuilder mensagemHtml = new StringBuilder();
			mensagemHtml.append("<b>Segue abaixo seus dados de acesso Pessoa Física para a loja virtual:</b><br/><br/>");
			mensagemHtml.append("<b>Login:</b> " + pessoaFisica.getEmail() + "<br/>");
			mensagemHtml.append("<b>Senha:</b> ").append(senha).append("<br/><br/>");
			mensagemHtml.append("<b>Muito obrigado!</b>");
			try {
				serviceSendEmail.enviarEmailHtml(
					"Acesso gerado para Loja Virtual",
					mensagemHtml.toString(),
					pessoaFisica.getEmail()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return pessoaFisica;
	}

	public CepDTO consultarCep(String cep) {
		return new RestTemplate().getForEntity("https://viacep.com.br/ws/" + cep + "/json/", CepDTO.class).getBody();
	}
	
	
	public ConsultaCnpjDTO consultaCnpjReceitaWS(String cnpj) {
		return new RestTemplate().getForEntity("https://receitaws.com.br/v1/cnpj/" + cnpj, ConsultaCnpjDTO.class).getBody();	}
	
	
}
