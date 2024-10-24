package com.enonic.xp.portal.impl.api;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public final class DynamicUniversalApiHandler
{
    private final UniversalApiHandler apiHandler;

    private final ApiDescriptor apiDescriptor;

    public DynamicUniversalApiHandler( final UniversalApiHandler apiHandler, final ApiDescriptor apiDescriptor )
    {
        this.apiHandler = apiHandler;
        this.apiDescriptor = apiDescriptor;
    }

    public WebResponse handle( WebRequest request )
    {
        return apiHandler.handle( request );
    }

    public UniversalApiHandler getApiHandler()
    {
        return apiHandler;
    }

    public ApiDescriptor getApiDescriptor()
    {
        return apiDescriptor;
    }
}
