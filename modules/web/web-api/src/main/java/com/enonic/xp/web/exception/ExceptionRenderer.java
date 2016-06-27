package com.enonic.xp.web.exception;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ExceptionRenderer
{
    WebResponse render( WebRequest req, WebException cause );
}
