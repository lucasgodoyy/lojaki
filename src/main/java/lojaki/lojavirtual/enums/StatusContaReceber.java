package lojaki.lojavirtual.enums;



public enum StatusContaReceber {

	COBRANCA("Pagar"), 
	VENCIDA("Vencida"), 
	ABERTA("Aberta"), 
	QUITADA("Quitada"),
	NEGOCIADA("Negociada");
	
	
	private String descricao;

	private StatusContaReceber(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}

}
