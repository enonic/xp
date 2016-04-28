package com.enonic.xp.web.handler;

import com.google.common.annotations.Beta;

@Beta
public interface WebHandlerChain
{
    void handle( WebRequest webRequest, WebResponse webResponse );
}
