package com.cipley.sample.model.exception;

import lombok.NonNull;

public class BadRequestException extends HttpException {
	private static final long serialVersionUID = 1883745829362792062L;

	public BadRequestException(@NonNull Short errorCode, @NonNull String errorMessage) {
		super(errorCode, errorMessage);
		this.errorCategory = ErrorCategory.BUSINESS;
	}
}
