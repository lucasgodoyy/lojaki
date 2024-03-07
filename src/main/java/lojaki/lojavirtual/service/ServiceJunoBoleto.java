package lojaki.lojavirtual.service;

import java.io.Serializable;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import lojaki.lojavirtual.enums.ApiTokenIntegracao;
import lojaki.lojavirtual.model.AccessTokenJunoAPI;
import lojaki.lojavirtual.repository.AccesTokenJunoRepository;
import lojaki.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;

@Service
public class ServiceJunoBoleto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AccessTokenJunoService accessTokenJunoService;

	@Autowired
	private AccesTokenJunoRepository accesTokenJunoRepository;

	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_virt_repository;

	public AccessTokenJunoAPI obterTokenApiJuno() throws Exception {

		AccessTokenJunoAPI accessTokenJunoAPI = accessTokenJunoService.buscaTokenAtivo();

		if (accessTokenJunoAPI == null || (accessTokenJunoAPI != null && accessTokenJunoAPI.expirado())) {

			String clienteID = "vi7QZerW09C8JG1o";
			String secretID = "$A_+&ksH}&+2<3VM]1MZqc,F_xif_-Dc";

			Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();

			WebResource webResource = client
					.resource("https://api.juno.com.br/authorization-server/oauth/token?grant_type=client_credentials");

			String basicChave = clienteID + ":" + secretID;
			String token_autenticao = DatatypeConverter.printBase64Binary(basicChave.getBytes());

			ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_FORM_URLENCODED)
					.type(MediaType.APPLICATION_FORM_URLENCODED)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Authorization", "Basic " + token_autenticao).post(ClientResponse.class);

			if (clientResponse.getStatus() == 200) { /* Sucesso */
				accesTokenJunoRepository.deleteAll();
				accesTokenJunoRepository.flush();

				AccessTokenJunoAPI accessTokenJunoAPI2 = clientResponse.getEntity(AccessTokenJunoAPI.class);
				accessTokenJunoAPI2.setToken_acesso(token_autenticao);

				accessTokenJunoAPI2 = accesTokenJunoRepository.saveAndFlush(accessTokenJunoAPI2);

				return accessTokenJunoAPI2;
			} else {
				return null;
			}

		} else {
			return accessTokenJunoAPI;
		}
	}

	public String geraChaveBoletoPix() throws Exception {

		AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();

		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		WebResource webResource = client.resource("https://api.juno.com.br/pix/keys");
		// WebResource webResource =
		// client.resource("https://api.juno.com.br/api-integration/pix/keys");

		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("X-API-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.post(ClientResponse.class, "{ \"type\": \"RANDOM_KEY\" }");

		// .header("X-Idempotency-Key", "chave-boleto-pix")
		return clientResponse.getEntity(String.class);

	}

}
