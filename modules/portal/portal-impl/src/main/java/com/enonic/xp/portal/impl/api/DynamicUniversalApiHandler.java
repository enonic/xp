package com.enonic.xp.portal.impl.api;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public final class DynamicUniversalApiHandler
    implements UniversalApiHandler
{
    final UniversalApiHandler apiHandler;

    private final ApiDescriptor apiDescriptor;

    DynamicUniversalApiHandler( final UniversalApiHandler apiHandler, final ApiDescriptor apiDescriptor )
    {
        this.apiHandler = apiHandler;
        this.apiDescriptor = apiDescriptor;
    }

    @Override
    public WebResponse handle( WebRequest request )
    {
        return apiHandler.handle( request );
    }

    public ApiDescriptor getApiDescriptor()
    {
        return apiDescriptor;
    }
}
