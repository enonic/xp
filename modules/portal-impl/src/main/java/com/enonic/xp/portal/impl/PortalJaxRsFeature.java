package com.enonic.xp.portal.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.portal.impl.exception.PortalExceptionMapper;

final class PortalJaxRsFeature
    implements Feature
{
    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( new PortalExceptionMapper() );
        return true;
    }
}
