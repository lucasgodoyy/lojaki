package lojaki.lojavirtual.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



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
		
		
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}
	
	
	
	
	

}
