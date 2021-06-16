package com.cipley.sample.model.exception;

import lombok.NonNull;

public class ServiceUnavailableException extends HttpException {
	private static final long serialVersionUID = -7034353617364772364L;

	public ServiceUnavailableException(@NonNull Short errorCode, @NonNull String errorMessage) {
		super(errorCode, errorMessage);
		this.errorCategory = ErrorCategory.TECHNICAL;
	}

}
