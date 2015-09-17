package com.enonic.xp.admin.impl.rest.resource;

import com.enonic.xp.web.jaxrs.impl.JaxRsResourceTestSupport;

public abstract class AdminResourceTestSupport
    extends JaxRsResourceTestSupport
{
    public AdminResourceTestSupport()
    {
        setBasePath( ResourceConstants.REST_ROOT );
    }
}
