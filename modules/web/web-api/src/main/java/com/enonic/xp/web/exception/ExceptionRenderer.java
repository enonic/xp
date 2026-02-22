package com.enonic.xp.web.exception;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ExceptionRenderer
{
    WebResponse render( WebRequest req, Exception cause );
}
