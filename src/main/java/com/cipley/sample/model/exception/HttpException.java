package com.cipley.sample.model.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class HttpException extends RuntimeException {
	private static final long serialVersionUID = -8689460002145780871L;

	protected ErrorCategory errorCategory;
	
	@NonNull
	protected Short errorCode;
	
	@NonNull
	protected String errorMessage; 
	
}
