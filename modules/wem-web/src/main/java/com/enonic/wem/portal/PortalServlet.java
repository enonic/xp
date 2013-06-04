package com.enonic.wem.portal;

import javax.inject.Singleton;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.container.filter.NormalizeFilter;

import com.enonic.wem.portal.dispatch.PortalDispatcherResource;
import com.enonic.wem.web.jaxrs.JaxRsServlet;

@Singleton
public final class PortalServlet
    extends JaxRsServlet
{
    @Override
    protected void configure()
    {
        setFeature( ResourceConfig.FEATURE_NORMALIZE_URI, true );
        setFeature( ResourceConfig.FEATURE_CANONICALIZE_URI_PATH, true );

        addClass( PortalDispatcherResource.class );
        addClass( NormalizeFilter.class );
    }
}
