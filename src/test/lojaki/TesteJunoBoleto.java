package lojaki;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import junit.framework.TestCase;
import lojaki.lojavirtual.LojakiApplication;
import lojaki.lojavirtual.model.dto.CriarWebHook;
import lojaki.lojavirtual.model.dto.ObjetoPostCarneJuno;
import lojaki.lojavirtual.service.ServiceAssasBoleto;
import lojaki.lojavirtual.service.ServiceJunoBoleto;

@Profile("dev")
@SpringBootTest(classes = LojakiApplication.class)
public class TesteJunoBoleto extends TestCase {

	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;

	@Autowired
	private ServiceAssasBoleto serviceAssasBoleto;

	
	 
	
	@Test
	public void testgerarCarneApiAsaas() throws Exception {

		ObjetoPostCarneJuno dados = new ObjetoPostCarneJuno();
		dados.setEmail("ehlucasgodoy@gmail.com");
		dados.setPayerName("lucas godoy");
		dados.setPayerCpfCnpj("05916564937");
		dados.setPayerPhone("45999795800");
		dados.setIdVenda(8L);

		String retorno = serviceAssasBoleto.gerarCarneApiAsaas(dados);

		System.out.println(retorno);
	}

	@Test
	public void testbuscaClientePessoaApiAsaas() throws Exception {

		ObjetoPostCarneJuno dados = new ObjetoPostCarneJuno();
		dados.setEmail("ehlucasgodoy@gmail.com");
		dados.setPayerName("lucas godoy");
		dados.setPayerCpfCnpj("77606657416");
		dados.setPayerPhone("51998615567");

		String customer_id = serviceAssasBoleto.buscaOuClientePessoaApiAsaas(dados);

		// System.out.println(customer_id);

		assertEquals("cus_000081108772", customer_id);
	}

	@Test
	public void testcriarChavePixAsaas() throws Exception {

		String chaveAPi = serviceAssasBoleto.criarChavePixAsaas();

		System.out.println("Chave Asass API" + chaveAPi);
	}

	@Test
	public void deleteWebHook() throws Exception {

		serviceJunoBoleto.deleteWebHook("wbh_E71095B5BF65E8D2DB018EE8A89BACB8");

	}

	@Test
	public void listaWebHook() throws Exception {

		String retorno = serviceJunoBoleto.listaWebHook();

		System.out.println(retorno);
	}

	@Test
	public void testeCriarWebHook() throws Exception {

		CriarWebHook criarWebHook = new CriarWebHook();
		criarWebHook.setUrl(
				"https://api2.lojavirtualjdev.com.br/loja_virtual_mentoria/requisicaojunoboleto/notificacaoapiv2");
		criarWebHook.getEventTypes().add("BILL_PAYMENT_STATUS_CHANGED");
		criarWebHook.getEventTypes().add("PAYMENT_NOTIFICATION");

		String retorno = serviceJunoBoleto.criarWebHook(criarWebHook);

		System.out.println(retorno);
	}

}
