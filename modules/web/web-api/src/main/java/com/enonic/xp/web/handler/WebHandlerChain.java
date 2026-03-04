package com.enonic.xp.web.handler;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;


public interface WebHandlerChain
{
    WebResponse handle( WebRequest webRequest, WebResponse webResponse )
        throws Exception;
}