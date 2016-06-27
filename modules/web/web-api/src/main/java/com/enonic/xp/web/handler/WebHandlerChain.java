package com.enonic.xp.web.handler;

import com.google.common.annotations.Beta;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Beta
public interface WebHandlerChain
{
    WebResponse handle( WebRequest webRequest, WebResponse webResponse )
        throws Exception;
}