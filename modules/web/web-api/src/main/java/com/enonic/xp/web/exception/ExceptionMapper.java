package com.enonic.xp.web.exception;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

public interface ExceptionMapper
{
    WebException map( final Throwable cause );

    void throwIfNeeded( final WebResponse res )
        throws WebException;
}
