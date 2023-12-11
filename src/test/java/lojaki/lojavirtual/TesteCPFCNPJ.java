package lojaki.lojavirtual;

import lojaki.lojavirtual.util.ValidaCNPJ;
import lojaki.lojavirtual.util.ValidaCPF;

public class TesteCPFCNPJ {

	public static void main(String[] args) {
		boolean isCnpj = ValidaCNPJ.isCNPJ("48178294558000150");
		System.out.println("cnpj válido: " + isCnpj);

		
		boolean isCpf = ValidaCPF.isCPF("443.374.34550-20");
		System.out.println("CPF válido: " + isCpf);
	}

}
