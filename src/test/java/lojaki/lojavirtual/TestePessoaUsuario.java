package lojaki.lojavirtual;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import junit.framework.TestCase;
import lojaki.lojavirtual.controller.PessoaController;
import lojaki.lojavirtual.model.PessoaJuridica;
import lojaki.lojavirtual.repository.PessoaJuridicaRepository;
import lojaki.lojavirtual.service.PessoaUserService;

@Profile("test")
@Transactional
@SpringBootTest
class TestePessoaUsuario extends TestCase{

	
	
	@Autowired
	private PessoaController pessoaController;
	
	
	@Test
	public void testCadPessoaFisica() throws ExceptionLojaki  {

		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj("94.723.655/0001-31");
		pessoaJuridica.setNome("joao");
		pessoaJuridica.setEmail("teste@gmail.com");
		pessoaJuridica.setTelefone("20");
		pessoaJuridica.setInscricaoEstadual("20");
		pessoaJuridica.setInscricaoMunicipal("20");
		pessoaJuridica.setNomeFantasia("20");
		pessoaJuridica.setRazaoSocial("20");
		
		pessoaController.salvarPj(pessoaJuridica);
		
	/*	PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setCpf("178.497.830-26");
		pessoaFisica.setNome("lucas");
		pessoaFisica.setEmail("lucas@123.com.br");
		pessoaFisica.setTelefone("9999999999");
		pessoaFisica.setEmpresa(pessoaFisica);

		
		Endereco endereco1 = new Endereco();
		endereco1.setBairro("Jd Dias");
		endereco1.setCep("65656656");
		endereco1.setComplemento("Casa zcinaza");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setNumero("389");
		endereco1.setPessoa(pessoaFisica);
		endereco1.setRuaLogra("Av. São João sexto");
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("PR");
		endereco1.setCidade("Curitiba");

		Endereco endereco2 = new Endereco();
		endereco2.setBairro("Jd Marana");
		endereco2.setCep("76767676");
		endereco2.setComplemento("Andar 4");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setNumero("555");
		endereco2.setPessoa(pessoaFisica);
		endereco2.setRuaLogra("Av. Maaringá");
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("PR");
		endereco2.setCidade("Maringá");

		pessoaFisica.getEnderecos().add(endereco1);
		pessoaFisica.getEnderecos().add(endereco2);

		pessoaFisica = pessoaController.salvarPf(pessoaFisica).getBody();

		assertEquals(true, pessoaFisica.getId() > 0);

		for (Endereco endereco : pessoaFisica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}

		assertEquals(2, pessoaFisica.getEnderecos().size());*/
	}
	
	
	
}
