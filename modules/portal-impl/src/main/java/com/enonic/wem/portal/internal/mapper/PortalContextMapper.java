package com.enonic.wem.portal.internal.mapper;

import java.util.Objects;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.script.mapper.ContentMapper;
import com.enonic.wem.script.mapper.PageComponentMapper;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;
import com.enonic.xp.portal.PortalContext;

public final class PortalContextMapper
    implements MapSerializable
{
    private final PortalContext context;

    public PortalContextMapper( final PortalContext context )
    {
        this.context = context;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        new PortalRequestMapper( this.context.getRequest() ).serialize( gen );
        serializeContent( gen, this.context.getContent() );
        serializeSite( gen, this.context.getSite() );
        serializePageDescriptor( gen, this.context.getPageDescriptor() );
        serializeComponent( gen, this.context.getComponent() );

        gen.value( "module", Objects.toString( this.context.getModule(), null ) );

    }

    private void serializeComponent( final MapGenerator gen, final PageComponent component )
    {
        if ( component != null )
        {
            gen.map( "component" );
            new PageComponentMapper( component ).serialize( gen );
            gen.end();
        }
    }

    private void serializePageDescriptor( final MapGenerator gen, final PageDescriptor pageDescriptor )
    {
        if ( pageDescriptor != null )
        {
            gen.map( "pageDescriptor" );
            gen.value( "key", pageDescriptor.getKey().toString() );
            gen.value( "name", pageDescriptor.getName().toString() );
            gen.value( "displayName", pageDescriptor.getDisplayName() );
            serializeRegions( gen, pageDescriptor.getRegions() );
            // serializeForm( gen, pageDescriptor.getConfig() ); TODO
            gen.end();
        }
    }

    private static void serializeRegions( final MapGenerator gen, final RegionDescriptors values )
    {
        gen.array( "regions" );
        for ( final RegionDescriptor region : values )
        {
            gen.value( region.getName() );
        }
        gen.end();
    }

    private void serializeContent( final MapGenerator gen, final Content content )
    {
        if ( content != null )
        {
            gen.map( "content" );
            new ContentMapper( content ).serialize( gen );
            gen.end();
        }
    }

    private void serializeSite( final MapGenerator gen, final Site site )
    {
        if ( site != null )
        {
            gen.map( "site" );
            new SiteMapper( site ).serialize( gen );
            gen.end();
        }
    }

}
