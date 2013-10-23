package com.enonic.wem.portal;

import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;

@Singleton
public final class PortalServlet
    extends HttpServlet
{
    /*
    @Override
    protected void configure()
    {
        setFeature( ResourceConfig.FEATURE_NORMALIZE_URI, true );
        setFeature( ResourceConfig.FEATURE_CANONICALIZE_URI_PATH, true );

        addClass( PortalDispatcherHandler.class );
        addClass( NormalizeFilter.class );
    }*/
}
