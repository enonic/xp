package com.enonic.xp.web.handler;

public interface WebExceptionMapper
{
    WebException map( final Throwable cause );

    void throwIfNeeded( final WebResponse res )
        throws WebException;
}
