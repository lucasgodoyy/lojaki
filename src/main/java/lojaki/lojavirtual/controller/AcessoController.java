package lojaki.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.service.AcessoService;



@RestController
public class AcessoController {

	
	
	@Autowired
	private AcessoService acessoService;
	
}
