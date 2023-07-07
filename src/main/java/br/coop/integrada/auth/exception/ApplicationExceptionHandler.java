package br.coop.integrada.auth.exception;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApplicationExceptionHandler {
	@ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DefaultExceptions methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors, ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public DefaultExceptions constraintViolationException(ConstraintViolationException ex) {
        return DefaultExceptions.construir(
                BAD_REQUEST.value(),
                "Não foi possível executar a operação!",
                ex.getMessage()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    public DefaultExceptions dataIntegrityViolationException(DataIntegrityViolationException ex) {
        return DefaultExceptions.construir(
                BAD_REQUEST.value(),
                "Tentativa de cadastro/atualização que viola a restrição de integridade!",
                ex.getMessage()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ObjectNotFoundException.class)
    public DefaultExceptions objectNotFoundException(ObjectNotFoundException ex) {
        return DefaultExceptions.construir(
                BAD_REQUEST.value(),
                "Não encontrado!",
                ex.getMessage()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(SenhaIncorretaException.class)
    public DefaultExceptions senhaIncorretaException(SenhaIncorretaException ex) {
        return DefaultExceptions.construir(
                BAD_REQUEST.value(),
                "Senha incorreta!",
                ex.getMessage()
        );
    }
    
//    @ResponseStatus(BAD_REQUEST)
//    @ResponseBody
//    @ExceptionHandler(Exception.class)
//    public DefaultExceptions Exception(Exception ex) {
//    	return DefaultExceptions.construir(BAD_REQUEST.value(),"Ops! Ocorreu um erro ao executar a operação", ex.getMessage());
//    }

    private DefaultExceptions processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors, String messageEx) {
    	DefaultExceptions exception = DefaultExceptions.construir(BAD_REQUEST.value(), "Validation error", messageEx);
    	
        for (org.springframework.validation.FieldError fieldError: fieldErrors) {
        	exception.getFieldErrors().add(addFieldError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return exception;
    }   
    

    public FieldErrorItem addFieldError(String field, String message) {
    	FieldErrorItem error = new FieldErrorItem();
    	error.setField(field);
    	error.setMessage(message);
    	return error;
    }

}
