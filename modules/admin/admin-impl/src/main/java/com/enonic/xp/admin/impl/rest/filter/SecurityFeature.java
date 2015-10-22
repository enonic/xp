package com.enonic.xp.admin.impl.rest.filter;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.SecurityService;

@Component(immediate = true)
@Provider
public final class SecurityFeature
    implements DynamicFeature, JaxRsComponent
{
    private SecurityService securityService;

    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        new BasicAuthFeature( this.securityService ).configure( resourceInfo, context );
        new RoleBasedSecurityFeature().configure( resourceInfo, context );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
