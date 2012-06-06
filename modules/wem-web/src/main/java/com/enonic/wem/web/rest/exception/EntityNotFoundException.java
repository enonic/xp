package com.enonic.wem.web.rest.exception;

import org.springframework.http.HttpStatus;

public final class EntityNotFoundException
    extends HttpStatusException
{
    public EntityNotFoundException()
    {
        super( HttpStatus.NOT_FOUND );
    }
}
