package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.Set;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeResource;
import com.enonic.xp.admin.impl.rest.resource.schema.content.FilterByContentResource;
import com.enonic.xp.admin.impl.rest.resource.schema.xdata.XDataResource;
import com.enonic.xp.jaxrs.JaxRsComponent;


@Provider
public final class CmsResourceDynamicFeature
    implements DynamicFeature, JaxRsComponent
{
    private final Set<Class<? extends JaxRsComponent>> supportedResources = Set.of(
        /*ContentResource.class, ContentImageResource.class, ContentIconResource.class, ContentMediaResource.class, IssueResource.class,
        PageResource.class, PageTemplateResource.class, FragmentResource.class*/
        FilterByContentResource.class, XDataResource.class, ContentTypeResource.class );


    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext context )
    {
        if ( supportedResources.contains( resourceInfo.getResourceClass() ) )
        {
            context.register( new CmsResourceFilter() );
        }
    }
}
