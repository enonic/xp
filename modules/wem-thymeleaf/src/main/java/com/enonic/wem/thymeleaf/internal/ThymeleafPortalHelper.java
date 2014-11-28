package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.url.PortalUrlBuilders;

public final class ThymeleafPortalHelper
{
    private final PortalContext context;

    public ThymeleafPortalHelper( final PortalContext context )
    {
        this.context = context;
    }

    public String createResourceUrl( final String path )
    {
        return new PortalUrlBuilders( this.context ).createResourceUrl( path ).toString();
    }
}
