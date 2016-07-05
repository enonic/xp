package com.enonic.xp.web.impl.handler;

import com.enonic.xp.web.WebRequest;

public interface RequestVerifier
{
    void verify( WebRequest request )
        throws Exception;
}
