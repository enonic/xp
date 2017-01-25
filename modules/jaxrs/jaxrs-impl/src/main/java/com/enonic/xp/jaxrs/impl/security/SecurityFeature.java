package com.enonic.xp.jaxrs.impl.security;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;

@Component(immediate = true, property = "group=admin")
@Provider
public final class SecurityFeature
    implements DynamicFeature, JaxRsComponent
{
    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        new RoleBasedSecurityFeature().configure( resourceInfo, context );
    }
}
