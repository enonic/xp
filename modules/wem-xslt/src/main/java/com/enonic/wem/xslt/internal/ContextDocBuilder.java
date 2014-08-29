package com.enonic.wem.xslt.internal;

import org.w3c.dom.Document;

import com.enonic.wem.api.content.page.AbstractRegions;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.url.PortalUrlBuilders;

final class ContextDocBuilder
{
    public Document createContextDoc()
        throws Exception
    {
        final PortalContext context = PortalContextAccessor.get();
        final DomBuilder builder = DomBuilder.create( "context" );

        final PortalUrlBuilders urlBuilders = new PortalUrlBuilders( context );
        final String baseUrl = urlBuilders.createResourceUrl( "" ).toString();
        builder.start( "baseUrl" ).text( baseUrl ).end();

        if ( context.getComponent() != null )
        {
            final LayoutRegions layoutRegions = context.getLayoutRegions();
            if ( layoutRegions != null )
            {
                createRegionElements( builder, layoutRegions );
            }
        }
        else
        {
            createRegionElements( builder, context.getPageRegions() );
        }

        return builder.getDocument();
    }

    private void createRegionElements( final DomBuilder builder, final AbstractRegions regions )
    {
        if ( regions == null )
        {
            return;
        }

        builder.start( "regions" );
        for ( Region region : regions )
        {
            builder.start( "region" );
            final String regionName = region.getName();
            builder.attribute( "name", regionName );
            builder.attribute( "path", region.getRegionPath().toString() );
            builder.start( "components" );
            for ( PageComponent component : region.getComponents() )
            {
                builder.start( "component" );
                builder.attribute( "name", component.getName().toString() );
                builder.attribute( "path", component.getPath().toString() );
                builder.attribute( "type", component.getType().toString() );
                builder.end();
            }
            builder.end();
            builder.end();
        }
        builder.end();
    }
}
