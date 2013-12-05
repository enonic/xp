package com.enonic.wem.portal;

import javax.inject.Singleton;

import com.sun.jersey.api.core.ResourceConfig;

import com.enonic.wem.portal.content.ContentResource;
import com.enonic.wem.portal.services.ServicesResource;
import com.enonic.wem.web.jaxrs.JaxRsServlet;

@Singleton
public final class PortalServlet
    extends JaxRsServlet
{
    @Override
    protected void configure()
    {
        setFeature( ResourceConfig.FEATURE_DISABLE_WADL, true );
        addClass( ContentResource.class );
        addClass( ServicesResource.class );
    }
}
