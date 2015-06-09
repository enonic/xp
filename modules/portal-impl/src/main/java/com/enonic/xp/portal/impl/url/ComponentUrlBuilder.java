package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.region.Component;
import com.enonic.xp.portal.url.ComponentUrlParams;

final class ComponentUrlBuilder
    extends PortalUrlBuilder<ComponentUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, resolvePath().toString() );

        final String component = resolveComponent();
        if ( component == null )
        {
            return;
        }

        appendPart( url, "_" );
        appendPart( url, "component" );
        appendPart( url, component );
    }

    private ContentPath resolvePath()
    {
        return new ContentPathResolver().
            portalRequest( this.portalRequest ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }

    private String resolveComponent()
    {
        if ( this.params.getComponent() != null )
        {
            return this.params.getComponent();
        }

        final Component component = this.portalRequest.getComponent();
        if ( component == null )
        {
            return null;
        }

        return component.getPath().toString();
    }
}
