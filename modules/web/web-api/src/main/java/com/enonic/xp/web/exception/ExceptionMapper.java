package com.enonic.xp.web.exception;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

public interface ExceptionMapper
{
    WebException map( Throwable cause );

    void throwIfNeeded( WebResponse res )
        throws WebException;
}
