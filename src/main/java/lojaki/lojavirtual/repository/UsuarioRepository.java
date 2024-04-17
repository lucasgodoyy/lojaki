package lojaki.lojavirtual.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import lojaki.lojavirtual.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Transactional
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(value = "UPDATE Usuario SET senha = ?1 WHERE login = ?2", nativeQuery = true)
	void updateSenhaUser(String senha, String login);


	@Query("SELECT u FROM Usuario u WHERE u.login = ?1")
	Usuario findUserByLogin(String login);

	
	@Query(value = "SELECT u FROM Usuario u WHERE u.pessoa.id = ?1 OR u.login = ?2")
    Usuario findUserByPessoa(Long id, String email);

	
	@Query("SELECT u FROM Usuario u WHERE u.dataAtualSenha <= current_date - 90")
	List<Usuario> obterUsuariosComSenhaVencida();
	
	
	@Query(value = "SELECT constraint_name \n" +
			"FROM information_schema.constraint_column_usage\n" +
			"WHERE table_name = 'usuarios_acesso'\n" +
			"AND column_name = 'acesso_id'\n" +
			"AND constraint_name <> 'unique_acesso_user'", nativeQuery = true)
	String consultaConstraintAcesso();

	
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO usuarios_acesso(usuario_id, acesso_id) VALUES (?1, (SELECT id FROM acesso WHERE descricao = 'ROLE_USER'))", nativeQuery = true)
	void insereAcessoUser(Long idUser);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO usuarios_acesso(usuario_id, acesso_id) VALUES (?1, (SELECT id FROM acesso WHERE descricao = ?2 limit 1))", nativeQuery = true)
	void insereAcessoUserPj(Long idUser, String acesso);
	
	
	
	
}
