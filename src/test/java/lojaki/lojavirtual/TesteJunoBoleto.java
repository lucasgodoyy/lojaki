package lojaki.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import junit.framework.TestCase;
import lojaki.lojavirtual.model.dto.CriarWebHook;
import lojaki.lojavirtual.service.ServiceJunoBoleto;

@Profile("dev")
@SpringBootTest(classes = LojakiApplication.class)
public class TesteJunoBoleto extends TestCase {

	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;

/*	@Test
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
				"https://api.lojakivirtual.com.br/lojaki/requisicaojunoboleto/notificacaoapiv2");
		criarWebHook.getEventTypes().add("BILL_PAYMENT_STATUS_CHANGED");
		criarWebHook.getEventTypes().add("PAYMENT_NOTIFICATION");

		String retorno = serviceJunoBoleto.criarWebHook(criarWebHook);

		System.out.println(retorno);
	} */

}
