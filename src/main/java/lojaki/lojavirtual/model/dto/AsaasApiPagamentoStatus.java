package lojaki.lojavirtual.model.dto;
/**
 * 
 * Armazena URL da API de Key da chave e tipos de pagamento
 * @author lucas
 *
 */
public class AsaasApiPagamentoStatus {
	
	
	public static String BOLETO = "BOLETO";
	public static String CREDIT_CARD = "CREDIT_CARD";
	public static String PIX = "PIX";
	public static String BOLETO_PIX = "UNDEFINED"; /*conbrança que pode ser paga por pir, boleto e cartão*/
	
	public static String URL_API_ASAAS = "https://www.asaas.com/api/v3/";
	
	public static String API_KEY = "$aact_YTU5YTE0M2M2N2I4MTliNzk0YTI5N2U5MzdjNWZmNDQ6OjAwMDAwMDAwMDAwMDA0MDc0MDM6OiRhYWNoX2ZmMzM3YjkwLTJkNmEtNDM3Ni05NzRkLWQ4ZjBhNzliZTEwMQ==";

}
