package lojaki.lojavirtual.model.dto;

import java.lang.reflect.Constructor;

public class MensagemSenhaDTO {
	
	
	private String msg;
	
	
	
	public MensagemSenhaDTO (String msg) {
		
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
