package com.enonic.xp.admin.impl.rest.filter;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.security.SecurityService;

final class BasicAuthFeature
    implements DynamicFeature
{
    private final SecurityService securityService;

    public BasicAuthFeature( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        context.register( new BasicAuthFilter( this.securityService ), Priorities.AUTHENTICATION );
    }
}
