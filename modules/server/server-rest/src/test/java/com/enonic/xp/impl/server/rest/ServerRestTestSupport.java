package com.enonic.xp.impl.server.rest;

import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;


public abstract class ServerRestTestSupport
    extends JaxRsResourceTestSupport
{
    public ServerRestTestSupport()
    {
        setBasePath( "/" );
    }
}
