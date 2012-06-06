package com.enonic.wem.web.rest.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public abstract class HttpStatusException
    extends RuntimeException
{
    private final HttpStatus status;
    private final HttpHeaders headers;

    public HttpStatusException(final HttpStatus status)
    {
        this.status = status;
        this.headers = new HttpHeaders();
    }

    public final HttpStatus getStatus()
    {
        return this.status;
    }

    public final HttpHeaders getHeaders()
    {
        return this.headers;
    }
}
