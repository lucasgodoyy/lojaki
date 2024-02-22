package lojaki.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lojaki.lojavirtual.ExceptionLojaki;
import lojaki.lojavirtual.model.CupomDesconto;
import lojaki.lojavirtual.repository.CupDescontoRepository;

@RestController
public class CupDescontoController {

	
	
	@Autowired
	private CupDescontoRepository cupDescontoRepository;
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc/{idEmpresa}")
	public ResponseEntity<List<CupomDesconto>> listaCupomDesc(@PathVariable("idEmpresa") Long idEmpresa){
		
		return new ResponseEntity<List<CupomDesconto>>(cupDescontoRepository.cupDescontoPorEmpresa(idEmpresa), HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc")
	public ResponseEntity<List<CupomDesconto>> listaCupomDesc(){
		
		return new ResponseEntity<List<CupomDesconto>>(cupDescontoRepository.findAll() , HttpStatus.OK);
	}
	
	
	@ResponseBody
	@PostMapping (value= "**/salvarCupomDesc")
	public ResponseEntity<CupomDesconto> salvarCupom (@RequestBody @Valid CupomDesconto cupomDesconto){
		
		CupomDesconto cupomDesconto2 = cupDescontoRepository.save(cupomDesconto);
		
		return new ResponseEntity<CupomDesconto>(cupomDesconto2, HttpStatus.OK);
		
		
	}
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteCupomDesc") /*Mapeando a url para receber JSON*/
	public ResponseEntity<?> deleteCupom(@RequestBody CupomDesconto cupomDesconto) { /*Recebe o JSON e converte pra Objeto*/
		
		cupDescontoRepository.deleteById(cupomDesconto.getId());
		
		return new ResponseEntity("Cupom removido",HttpStatus.OK);
	}
	
	//Editar
	@ResponseBody
	@GetMapping(value = "**/obterMarcaProduto/{id}")
	public ResponseEntity<CupomDesconto> obterCupom(@PathVariable("id") Long id) throws ExceptionLojaki { 
		
		CupomDesconto cupomDesconto = cupDescontoRepository.findById(id).orElse(null);
		
		if (cupomDesconto == null) {
			throw new ExceptionLojaki("Não encontrou Cupom com código: " + id);
		}
		
		return new ResponseEntity<CupomDesconto>(cupomDesconto,HttpStatus.OK);
	}
	
	

}