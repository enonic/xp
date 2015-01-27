package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.api.util.Reference;
import com.enonic.wem.core.content.page.region.RegionDataSerializer;

public class PageDataSerializer
    extends AbstractDataSetSerializer<Page, Page>
{
    private static final String CONTROLLER = "controller";

    private static final String TEMPLATE = "template";

    private static final String CONFIG = "config";

    private static final String REGION = "region";

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
        asSet.addReference( TEMPLATE, page.hasTemplate() ? Reference.from( page.getTemplate().toString() ) : null );

        if ( page.hasRegions() )
        {
            if ( !page.getRegions().isEmpty() )
            {
                for ( Region region : page.getRegions() )
                {
                    regionDataSerializer.toData( region, asSet );
                }
            }
            else
            {
                asSet.addSet( regionDataSerializer.getPropertyName(), null );
            }
        }

        if ( page.hasConfig() )
        {
            asSet.addSet( CONFIG, page.getConfig().getRoot().copy( asSet.getTree() ) );
        }
    }

    public Page fromData( final PropertySet asData )
    {
        final Page.Builder page = Page.newPage();
        if ( asData.isNotNull( CONTROLLER ) )
        {
            page.controller( DescriptorKey.from( asData.getString( CONTROLLER ) ) );
        }
        if ( asData.isNotNull( TEMPLATE ) )
        {
            page.template( PageTemplateKey.from( asData.getReference( TEMPLATE ).toString() ) );
        }
        if ( asData.hasProperty( REGION ) )
        {
            final PageRegions.Builder pageRegionsBuilder = PageRegions.newPageRegions();
            for ( final Property regionAsProp : asData.getProperties( REGION ) )
            {
                if ( regionAsProp.hasNotNullValue() )
                {
                    pageRegionsBuilder.add( regionDataSerializer.fromData( regionAsProp.getSet() ) );
                }
            }
            page.regions( pageRegionsBuilder.build() );
        }
        if ( asData.hasProperty( CONFIG ) )
        {
            page.config( asData.getSet( CONFIG ).toTree() );
        }
        return page.build();
    }

}
