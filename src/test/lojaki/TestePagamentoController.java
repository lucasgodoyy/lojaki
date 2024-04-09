package lojaki;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import junit.framework.TestCase;
import lojaki.lojavirtual.LojakiApplication;
import lojaki.lojavirtual.controller.PagamentoController;
import lojaki.lojavirtual.controller.RecebePagamentoWebHookApiJuno;

@Profile("dev")
@SpringBootTest(classes = LojakiApplication.class)
public class TestePagamentoController extends TestCase{

	
	@Autowired
	private PagamentoController pagamentoController;
	
	@Autowired
	private RecebePagamentoWebHookApiJuno recebePagamentoWebHookApiJuno;
	
	@Autowired
	private WebApplicationContext wac;
	
	@Test
	public void testRecebeNotificacaoPagamentoApiAsaas() throws Exception {
		
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		String json = new String(Files.readAllBytes(Paths.get("C:\\workspace-spring-tool-suite\\loja_virtual_mentoria\\src\\test\\java\\jdev\\mentoria\\lojavirtual\\jsonwebhookasaas.txt")));
		
		ResultActions retornoApi = mockMvc.perform(MockMvcRequestBuilders.post("/requisicaojunoboleto/notificacaoapiasaas")
				.content(json)
				.accept("application/json;charset=UTF-8")
				.contentType("application/json;charset=UTF-8"));
		
	 System.out.println(retornoApi.andReturn().getRequest().getContentAsString());
		
		
	}
	
	
	@Test
	public void testfinalizarCompraCartaoAsaas() throws Exception {
		pagamentoController.finalizarCompraCartaoAsaas("5126462892278565", "Alex F Egidio",
													  "612", "06",
													  "2025", 15L, "05916564937",
													  2, "87025758", "Pioneiro antonio de ganello",
													  "365", "PR", "Maringá");
	}

}

