package lojaki.lojavirtual.service;


import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	
	
	public boolean possuiAcesso(String username, String acesso) {
		
		String sql = "select count(1) > 0 from usuarios_acesso as ua "
				 +" inner join usuario as u on u.id = ua.usuario_id "
				 +" inner join acesso as a on a.id = ua.acesso_id "
				 +" where u.login = '"+username+"' "
				 +" and a.descricao in ("+acesso+ ")";
				 
		Query query = entityManager.createNativeQuery(sql);
		
		


		return Boolean.valueOf(query.getSingleResult().toString());
		
	}
	
	
	

	public PessoaJuridica salvarPessoaJuridica(PessoaJuridica pessoaJuridica) {

		
		for (int i = 0; i< pessoaJuridica.getEnderecos().size(); i++) {
			pessoaJuridica.getEnderecos().get(i).setPessoa(pessoaJuridica);
			pessoaJuridica.getEnderecos().get(i).setEmpresa(pessoaJuridica.getEmpresa());
		}
		
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
			
			
			System.out.println(usuarioPj.getSenha() + "--------- " + usuarioPj.getLogin());

			
/*			// Envio e-mail login e senha
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
			} */

		}
		return pessoaJuridica;

	}
	
	
		public PessoaJuridica salvarPessoaJuridicaNova(PessoaJuridica juridica) {

		
		PessoaJuridica juridicaSalva = pessoaJuridicaRepository.save(juridica);
		
		
		Usuario usuarioPj = usuarioRepository.findUserByPessoa(juridicaSalva.getId(), juridicaSalva.getEmail());

		if (usuarioPj == null) {
			
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
			}

			usuarioPj = new Usuario();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
		
			
			usuarioPj.setEmpresa(juridicaSalva);
			
			
			
			usuarioPj.setPessoa(juridicaSalva);
			usuarioPj.setLogin(juridicaSalva.getEmail());

			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

			usuarioPj.setSenha(senhaCriptografada);
			Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuarioPj);

			System.out.println("Senha: " + senha + "--------- " + "Login: " + usuarioSalvo.getLogin());

			
			usuarioRepository.insereAcessoUser(usuarioSalvo.getId());
			usuarioRepository.insereAcessoUserPj(usuarioSalvo.getId(), "ROLE_ADMIN");
			
			
			
			
			//Envio e-mail login e senha
			StringBuilder mensagemHtml = new StringBuilder();
			mensagemHtml.append("<b>Olá, segue abaixo seus dados de acesso para a loja</b><br/><br/>");
			mensagemHtml.append("<b>Login:</b> " + juridicaSalva.getEmail() + "<br/>");
			mensagemHtml.append("<b>Senha:</b> ").append(senha).append("<br/><br/>");
			mensagemHtml.append("<b>Obrigado!</b>");
			try {
				serviceSendEmail.enviarEmailHtml("Cadastro realizado com sucesso", mensagemHtml.toString(),
						juridicaSalva.getEmail());
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}
		
		return juridicaSalva;

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
