package lojaki.lojavirtual;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;
import lojaki.lojavirtual.controller.AcessoController;
import lojaki.lojavirtual.model.Acesso;
import lojaki.lojavirtual.repository.AcessoRepository;

@Transactional
@SpringBootTest
class LojakiApplicationTests extends TestCase{

	@Autowired
	private AcessoController acessoController;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	@Autowired
	private WebApplicationContext wac;
	
	
	/*Teste end point salvarAcesso*/
	@Test
	void testRestApiCadastroAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_COMPRADORA");
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc.perform(
				MockMvcRequestBuilders
								.post("/salvarAcesso")
								.content(mapper.writeValueAsString(acesso))
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON));		
		
		Acesso objetoRetorno = mapper.readValue(
			retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class);
		
		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
		//assertNotNull(objetoRetorno.getId());
	}
	
	
	@Test
	void testRestApiDeleteAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_TEST_DELETE");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc.perform(
			MockMvcRequestBuilders
				.post("/deleteAcesso")
				.content(mapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		);		
		
		assertEquals("Acesso removido com sucesso!", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	}
	
	
	
	
	@Test
	void testRestApiDeleteAcessoPorId() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_TEST_DELETE");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc.perform(
			MockMvcRequestBuilders
				.delete("/deleteAcessoPorId/" + acesso.getId())
				.content(mapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		);		
		
		assertEquals("Acesso removido com sucesso!", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	}
	
	
	
	
	@Test
	void testRestApiObterAcessoPorId() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_OBTER_POR_ID");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc.perform(
			MockMvcRequestBuilders
				.get("/obterAcessoPorId/" + acesso.getId())
				.content(mapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		);		
		
		Acesso acessoRetorno = mapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class);
		
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		assertEquals(acesso.getDescricao(), acessoRetorno.getDescricao());
		assertEquals(acesso.getId(), acessoRetorno.getId());
	}
	
	
	
	@Test
	void testRestApiObterAcessoPorDescricao() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_OBTER_POR_DESC");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc.perform(
			MockMvcRequestBuilders
				.get("/obterAcessoPorDescricao/OBTER_POR_DESC")
				.content(mapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		);		
		
		List<Acesso> retornoListaApi = mapper.readValue(
			retornoApi.andReturn().getResponse().getContentAsString(), new TypeReference<>(){}
		);
		
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		assertEquals(1, retornoListaApi.size());
		assertEquals(acesso.getDescricao(), retornoListaApi.get(0).getDescricao());
		
		acessoRepository.deleteById(acesso.getId());
	}
	
	
	
	
	
	@Test
	public void testCadastroAcesso()  {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_TESTE8");
		
		assertEquals(true, acesso.getId() == null );
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		assertEquals(true, acesso.getId() > 0 );
		
		assertEquals("ROLE_TESTE8", acesso.getDescricao());
		
		
		/*teste carregamento*/
		
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		assertEquals(acesso.getId(), acesso.getId());
		
		
		
		/*teste de delete*/
		
		
		acessoRepository.deleteById(acesso2.getId());
		acessoRepository.flush();
		Acesso acesso3 = acessoRepository.findById(acesso2.getId()).orElse(null);
		assertEquals(true, acesso3 == null );
		
		
		/*teste query*/
		
		acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ALUNO");
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		List<Acesso> acessos = acessoRepository.buscarAcessoDesc("A".trim().toUpperCase());
		
		
		assertEquals(1, acessos.size());
		acessoRepository.deleteById(acesso.getId());
		
		
		
	}
	

}
