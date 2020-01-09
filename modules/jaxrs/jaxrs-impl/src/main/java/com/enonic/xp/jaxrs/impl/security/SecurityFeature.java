package com.enonic.xp.jaxrs.impl.security;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.jaxrs.JaxRsComponent;

public final class SecurityFeature
    implements DynamicFeature, JaxRsComponent
{
    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        new RoleBasedSecurityFeature().configure( resourceInfo, context );
    }
}
