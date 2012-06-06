package com.enonic.wem.web.rest.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

public final class EntityRedirectException
    extends HttpStatusException
{
    public EntityRedirectException( final URI uri )
    {
        super( HttpStatus.SEE_OTHER );
        getHeaders().setLocation( uri );
    }
}
