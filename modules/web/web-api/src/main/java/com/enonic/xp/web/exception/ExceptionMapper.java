package com.enonic.xp.web.exception;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

@NullMarked
public interface ExceptionMapper
{
    WebException map( Throwable cause );

    void throwIfNeeded( WebResponse res )
        throws WebException;
}
