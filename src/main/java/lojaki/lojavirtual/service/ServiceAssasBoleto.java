package lojaki.lojavirtual.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import jdev.mentoria.lojavirtual.service.HostIgnoringCliente;
import lojaki.lojavirtual.model.BoletoJuno;
import lojaki.lojavirtual.model.VendaCompraLojaVirtual;
import lojaki.lojavirtual.model.dto.AsaasApiPagamentoStatus;
import lojaki.lojavirtual.model.dto.ClienteAsaasApiPagamento;
import lojaki.lojavirtual.model.dto.CobrancaApiAsaas;
import lojaki.lojavirtual.model.dto.CobrancaGeradaAsassApi;
import lojaki.lojavirtual.model.dto.CobrancaGeradaAssasData;
import lojaki.lojavirtual.model.dto.ObjetoPostCarneJuno;
import lojaki.lojavirtual.model.dto.ObjetoQrCodePixAsaas;
import lojaki.lojavirtual.repository.AccesTokenJunoRepository;
import lojaki.lojavirtual.repository.BoletoJunoRepository;
import lojaki.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;
import lojaki.lojavirtual.util.ValidaCPF;

@Service
public class ServiceAssasBoleto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AccessTokenJunoService accessTokenJunoService;

	@Autowired
	private AccesTokenJunoRepository accesTokenJunoRepository;

	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_virt_repository;

	@Autowired
	private BoletoJunoRepository boletoJunoRepository;

	/**
	 * Cria uma chave pix da minha conta da Asass para receber PIX;
	 * 
	 * @return chave
	 */

	public String criarChavePixAsaas() throws Exception {

		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource = client.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "pix/addressKeys");

		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.post(ClientResponse.class, "{\"type\":\"EVP\"}");

		String strinRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();
		return strinRetorno;

	}

	/**
	 *
	 * cus_000081108472 Retorna o id do Customer (Pessoa/cliente)
	 */
	public String buscaOuClientePessoaApiAsaas(ObjetoPostCarneJuno dados) throws Exception {

		/* id do cliente para ligar com a conbrança */
		String customer_id = "";

		/*--------------INICIO - criando ou consultando o cliente*/

		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource = client
				.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "customers?email=" + dados.getEmail());

		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);

		LinkedHashMap<String, Object> parser = new JSONParser(clientResponse.getEntity(String.class)).parseObject();
		clientResponse.close();

		/* Para saber se existe um client */
		Integer total = Integer.parseInt(parser.get("totalCount").toString());

		if (total <= 0) { /* Cria o cliente */

			ClienteAsaasApiPagamento clienteAsaasApiPagamento = new ClienteAsaasApiPagamento();

			if (!ValidaCPF.isCPF(dados.getPayerCpfCnpj())) {
				clienteAsaasApiPagamento.setCpfCnpj("60051803046");
			} else {
				clienteAsaasApiPagamento.setCpfCnpj(dados.getPayerCpfCnpj());
			}

			clienteAsaasApiPagamento.setEmail(dados.getEmail());
			clienteAsaasApiPagamento.setName(dados.getPayerName());
			clienteAsaasApiPagamento.setPhone(dados.getPayerPhone());

			Client client2 = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
			WebResource webResource2 = client2.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "customers");

			ClientResponse clientResponse2 = webResource2.accept("application/json;charset=UTF-8")
					.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
					.post(ClientResponse.class, new ObjectMapper().writeValueAsBytes(clienteAsaasApiPagamento));

			LinkedHashMap<String, Object> parser2 = new JSONParser(clientResponse2.getEntity(String.class))
					.parseObject();
			clientResponse2.close();

			customer_id = parser2.get("id").toString();

		} else {/* Já tem cliente cadastrado */
			List<Object> data = (List<Object>) parser.get("data");
			customer_id = new Gson().toJsonTree(data.get(0)).getAsJsonObject().get("id").toString().replaceAll("\"",
					"");
		}

		return customer_id;

	}

	public String gerarCarneApiAsaas(ObjetoPostCarneJuno objetoPostCarneJuno) throws Exception {

		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
				.findById(objetoPostCarneJuno.getIdVenda()).get();

		CobrancaApiAsaas cobrancaApiAsaas = new CobrancaApiAsaas();
		cobrancaApiAsaas.setCustomer(this.buscaOuClientePessoaApiAsaas(objetoPostCarneJuno));

		/* PIX, BOLETO OU UNDEFINED */
		cobrancaApiAsaas.setBillingType("UNDEFINED"); /* Gerando tanto PIX quanto Boleto */
		cobrancaApiAsaas
				.setDescription("Pix ou Boleto gerado para ao cobrança, cód: " + vendaCompraLojaVirtual.getId());
		cobrancaApiAsaas.setInstallmentValue(vendaCompraLojaVirtual.getValorTotal().floatValue());
		cobrancaApiAsaas.setInstallmentCount(1);

		Calendar daVencimento = Calendar.getInstance();
		daVencimento.add(Calendar.DAY_OF_MONTH, 7);
		cobrancaApiAsaas.setDueDate(new SimpleDateFormat("yyyy-MM-dd").format(daVencimento.getTime()));

		cobrancaApiAsaas.getInterest().setValue(1F);
		cobrancaApiAsaas.getFine().setValue(1F);

		String json = new ObjectMapper().writeValueAsString(cobrancaApiAsaas);
		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource = client.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments");

		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.post(ClientResponse.class, json);

		String stringRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();

		/* Buscando parcelas geradas */

		LinkedHashMap<String, Object> parser = new JSONParser(stringRetorno).parseObject();
		String installment = parser.get("installment").toString();
		Client client2 = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource2 = client2
				.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments?installment=" + installment);
		ClientResponse clientResponse2 = webResource2.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);

		String retornoCobrancas = clientResponse2.getEntity(String.class);
		// Buscando parcelas geradas 

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		CobrancaGeradaAsassApi listaCobranca = objectMapper.readValue(retornoCobrancas,
				new TypeReference<CobrancaGeradaAsassApi>() {
				});

		List<BoletoJuno> boletoJunos = new ArrayList<BoletoJuno>();
		int recorrencia = 1;
		for (CobrancaGeradaAssasData data : listaCobranca.getData()) {

			BoletoJuno boletoJuno = new BoletoJuno();

			boletoJuno.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			boletoJuno.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
			boletoJuno.setCode(data.getId());
			boletoJuno.setLink(data.getInvoiceUrl());
			boletoJuno.setDataVencimento(new SimpleDateFormat("yyyy-MM-dd")
					.format(new SimpleDateFormat("yyyy-MM-dd").parse(data.getDueDate())));
			boletoJuno.setCheckoutUrl(data.getInvoiceUrl());
			boletoJuno.setValor(new BigDecimal(data.getValue()));
			boletoJuno.setIdChrBoleto(data.getId());
			boletoJuno.setInstallmentLink(data.getInvoiceUrl());
			boletoJuno.setRecorrencia(recorrencia);

			// boletoJuno.setIdPix(c.getPix().getId());

			ObjetoQrCodePixAsaas codePixAsaas = this.buscarQrCodeCodigoPix(data.getId());

			boletoJuno.setPayloadInBase64(codePixAsaas.getPayload());
			boletoJuno.setImageInBase64(codePixAsaas.getEncodedImage());

			boletoJunos.add(boletoJuno);
			recorrencia++;
		}

		boletoJunoRepository.saveAllAndFlush(boletoJunos);

		return boletoJunos.get(0).getCheckoutUrl();

	}

	public ObjetoQrCodePixAsaas buscarQrCodeCodigoPix(String idCobranca) throws Exception {

		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource = client
				.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments/" + idCobranca + "/pixQrCode");

		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json").header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);

		String stringRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();

		ObjetoQrCodePixAsaas codePixAsaas = new ObjetoQrCodePixAsaas();

		LinkedHashMap<String, Object> parser = new JSONParser(stringRetorno).parseObject();
		codePixAsaas.setEncodedImage(parser.get("encodedImage").toString());
		codePixAsaas.setPayload(parser.get("payload").toString());

		return codePixAsaas;
	}

}
