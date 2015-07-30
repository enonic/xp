package com.enonic.xp.portal.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.portal.impl.exception.PortalExceptionMapper;
import com.enonic.xp.portal.impl.services.PortalServices;

final class PortalJaxRsFeature
    implements Feature
{
    private PortalExceptionMapper portalExceptionMapper;

    private PortalServices portalServices;

    @Override
    public boolean configure( final FeatureContext context )
    {
        this.portalExceptionMapper = new PortalExceptionMapper();
        this.portalExceptionMapper.setPortalServices( this.portalServices );

        context.register( portalExceptionMapper );
        return true;
    }

    public final void setServices( final PortalServices portalServices )
    {
        this.portalServices = portalServices;
        if ( this.portalExceptionMapper != null )
        {
            this.portalExceptionMapper.setPortalServices( portalServices );
        }
    }
}
