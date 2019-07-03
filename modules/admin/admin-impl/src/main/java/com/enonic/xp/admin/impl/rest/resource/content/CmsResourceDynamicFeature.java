package com.enonic.xp.admin.impl.rest.resource.content;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.enonic.xp.jaxrs.JaxRsComponent;


@Provider
public class CmsResourceDynamicFeature
    implements DynamicFeature, JaxRsComponent
{
    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        if ( ContentResource.class.equals( resourceInfo.getResourceClass() ) )
        {
            context.register( new CmsResourceFilter() );
        }
    }
}
