package lojaki.lojavirtual;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lojaki.lojavirtual.model.dto.ObjetoErroDTO;
import lojaki.lojavirtual.service.ServiceSendEmail;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {

	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	@ExceptionHandler(ExceptionLojaki.class)
	public ResponseEntity<Object> handleExceptionCustom(ExceptionLojaki ex) {
		
		ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
		
		objetoErroDTO.setError(ex.getMessage());
		objetoErroDTO.setCode(HttpStatus.OK.toString());
		
		return new ResponseEntity<>(objetoErroDTO, HttpStatus.OK);
	}
	
	
	
	@ExceptionHandler({ Exception.class, RuntimeException.class, Throwable.class })
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

		String msg = "";

		if (ex instanceof MethodArgumentNotValidException) {
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			for (ObjectError objectError : list) {
				msg = objectError.getDefaultMessage() + "\n";
			}
		} 
		
		else if (ex instanceof HttpMessageNotReadableException) {
			msg = "Não está sendo enviado dados para requisição!";
		}
		
		else {
			msg = ex.getMessage();
		}

		objetoErroDTO.setError(msg);
		objetoErroDTO.setCode(status.value() + " ==> " + status.getReasonPhrase());

		ex.printStackTrace();
		
		try {
			serviceSendEmail.enviarEmailHtml("Erro na operação", ExceptionUtils.getStackTrace(ex),
					 "ehlucasgodoy10@gmail.com");
			} 
		catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	

	// erros de banco
	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, SQLException.class })
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {

		ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

		String msg = "";

		if (ex instanceof DataIntegrityViolationException) {
			msg = "Erro de integridade no banco:"
					+ ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
		} 
		else if (ex instanceof ConstraintViolationException) {
			msg = "Erro de violação de constrante/chave estrangeira:"
					+ ((ConstraintViolationException) ex).getCause().getCause().getMessage();
		} 
		else if (ex instanceof SQLException) {
			msg = "Erro de SQL do banco:" + ((SQLException) ex).getCause().getCause().getMessage();
		} 
		else {
			msg = ex.getMessage();
		}

		objetoErroDTO.setError(msg);
		objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());

		ex.printStackTrace();

		try {
			serviceSendEmail.enviarEmailHtml("Erro na operação", ExceptionUtils.getStackTrace(ex),
					 "ehlucasgodoy10@gmail.com");
			} 
		catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
