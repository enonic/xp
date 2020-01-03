package com.enonic.xp.web.handler;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@PublicApi
public interface WebHandlerChain
{
    WebResponse handle( WebRequest webRequest, WebResponse webResponse )
        throws Exception;
}