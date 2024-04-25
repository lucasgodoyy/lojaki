package lojaki.lojavirtual.security;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lojaki.lojavirtual.ApplicationContextLoad;
import lojaki.lojavirtual.model.Usuario;
import lojaki.lojavirtual.repository.UsuarioRepository;



				/*Criar e retornar a autenticação JWT*/
@Service
@Component
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME = 959990000;

	private static final String SECRET = "qualquer-senha-secreta";

	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";
	
	
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		// Montagem do Token
		
		
		
		String jwt = Jwts.builder()
					.setSubject(username)
					.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.signWith(SignatureAlgorithm.HS512, SECRET)
					.compact();
		
		String token = TOKEN_PREFIX + " " + jwt;
		
		response.addHeader(HEADER_STRING, token);
		
		liberarCorsPolicy(response);
		
		
		Usuario usuario = ApplicationContextLoad.getApplicationContext()
				.getBean(UsuarioRepository.class)
				.findUserByLogin(username);
		
		
		response.getWriter().write("{\"Authorization\": \"" + token + "\", \"username\" : \"" + username + "\" , \"empresa\" : \"" + usuario.getEmpresa().getId() + "\"}");
	}
	
	
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = request.getHeader(HEADER_STRING);
		
		try {
		
			if (nonNull(token)) {
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				String user = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(tokenLimpo) 
					.getBody() /*Extraindo o usuario do token que fica no Body*/
					.getSubject();
				
				if (nonNull(user)){
					Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class)
						.findUserByLogin(user);
					
					if (nonNull(usuario)) {
						return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities()
						);
					}
				}
			}
		} catch(SignatureException e) {
			response.getWriter().write("O token está inválido!");
			} 
		catch(ExpiredJwtException e) {
			response.getWriter().write("O token está expirado! Efetue o login novamente!");
		} catch(MalformedJwtException e) {
			response.getWriter().write("O token está mal formado! Utilize um outro token!");
		} finally {
			liberarCorsPolicy(response);
		}
		return null;
	}
	
	
	private void liberarCorsPolicy(HttpServletResponse response) {
		if (isNull(response.getHeader("Access-Control-Allow-Origin"))) {
				response.addHeader("Access-Control-Allow-Origin", "*");		
		}
		if (isNull(response.getHeader("Access-Control-Allow-Headers"))) {
				response.addHeader("Access-Control-Allow-Headers", "*");		
		}
		if (isNull(response.getHeader("Access-Control-Request-Headers"))) {
				response.addHeader("Access-Control-Request-Headers", "*");		
		}
		if (isNull(response.getHeader("Access-Control-Allow-Methods"))) {
				response.addHeader("Access-Control-Allow-Methods", "*");		
		}
	}
	

}
