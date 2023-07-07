package br.coop.integrada.auth.exception;

import lombok.Data;

@Data
public class FieldErrorItem {
	private String message;
	private String field;
	
	public static FieldErrorItem construir(String field, String message) {
		FieldErrorItem error = new FieldErrorItem();
		error.setField(field);
		error.setMessage(message);
		return error;
	}
}