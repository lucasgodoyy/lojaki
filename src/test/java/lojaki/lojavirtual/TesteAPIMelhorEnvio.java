package lojaki.lojavirtual;

import lojaki.lojavirtual.enums.ApiTokenIntegracao;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TesteAPIMelhorEnvio {

	public static void main(String[] args) throws Exception {

		/* OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{\"options\":{\"receipt\":true,\"own_hand\":true,\"reverse\":true,\"non_commercial\":true}}");
		Request request = new Request.Builder()
		  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX +"api/v2/me/cart")
		  .post(body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "ehlucasgodoy10@gmail.com")
		  .build();

		Response response = client.newCall(request).execute();
		
		System.out.println(response.body().string());
		
		
		
	} */
	
	OkHttpClient client = new OkHttpClient();

	Request request = new Request.Builder()
	  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/agencies")
	  .get()
	  .addHeader("accept", "application/json")
	  .addHeader("User-Agent", "ehlucasgodoy10@gmail.com")
	  .build();

	Response response = client.newCall(request).execute();
	
	System.out.println(response.body().string());
	}
}
