package lojaki.lojavirtual;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
