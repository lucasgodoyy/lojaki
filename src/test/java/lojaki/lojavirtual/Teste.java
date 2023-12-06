package lojaki.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import junit.framework.TestCase;
import lojaki.lojavirtual.model.Acesso;
import lojaki.lojavirtual.repository.AcessoRepository;
import lojaki.lojavirtual.service.AcessoService;



@Profile("test")
@SpringBootTest(classes = LojakiApplication.class)
public class Teste extends TestCase{
	
	
	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	
	@Test
	public void testCadastroAcesso()  {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");
		
		acessoRepository.save(acesso);
		
	}
	

}
