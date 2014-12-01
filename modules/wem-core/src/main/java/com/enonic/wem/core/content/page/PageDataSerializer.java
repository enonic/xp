package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionDataSerializer;
import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.PropertySet;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public class PageDataSerializer
    extends AbstractDataSetSerializer<Page, Page>
{
    public static final String CONTROLLER = "controller";

    public static final String PAGE_TEMPLATE = "template";

    public static final String PAGE_CONFIG = "config";

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    private final String propertyName;

    public PageDataSerializer( final String propertyName )
    {
        this.propertyName = propertyName;
    }

    public void toData( final Page page, final PropertySet parent )
    {
        final PropertySet asSet = parent.addSet( propertyName );

        asSet.addString( CONTROLLER, page.hasController() ? page.getController().toString() : null );
        asSet.addString( PAGE_TEMPLATE, page.hasTemplate() ? page.getTemplate().toString() : null );

        if ( page.hasRegions() )
        {
            for ( Region region : page.getRegions() )
            {
                regionDataSerializer.toData( region, asSet );
            }
        }
        if ( page.hasConfig() )
        {
            asSet.addSet( PAGE_CONFIG, page.getConfig().getRoot().copy( asSet.getTree() ) );
        }
    }

    public Page fromData( final PropertySet asData )
    {
        final Page.Builder page = Page.newPage();
        if ( asData.isNotNull( CONTROLLER ) )
        {
            page.controller( PageDescriptorKey.from( asData.getString( CONTROLLER ) ) );
        }
        if ( asData.isNotNull( PAGE_TEMPLATE ) )
        {
            page.template( PageTemplateKey.from( asData.getString( PAGE_TEMPLATE ) ) );
        }
        final PageRegions.Builder pageRegionsBuilder = PageRegions.newPageRegions();
        for ( final Property regionAsProp : asData.getProperties( "region" ) )
        {
            pageRegionsBuilder.add( regionDataSerializer.fromData( regionAsProp.getSet() ) );
        }
        page.regions( pageRegionsBuilder.build() );
        if ( asData.hasProperty( PAGE_CONFIG ) )
        {
            page.config( asData.getSet( PAGE_CONFIG ).toTree() );
        }
        return page.build();
    }

}
