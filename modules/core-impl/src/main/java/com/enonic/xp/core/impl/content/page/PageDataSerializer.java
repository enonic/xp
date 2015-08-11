package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.core.impl.content.page.region.RegionDataSerializer;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.Region;
import com.enonic.xp.support.serializer.AbstractDataSetSerializer;
import com.enonic.xp.util.Reference;

public class PageDataSerializer
    extends AbstractDataSetSerializer<Page, Page>
{
    private static final String CONTROLLER = "controller";

    private static final String TEMPLATE = "template";

    private static final String CONFIG = "config";

    private static final String REGION = "region";

    private static final String CUSTOMIZED = "customized";

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    private final String propertyName;

    public PageDataSerializer( final String propertyName )
    {
        this.propertyName = propertyName;
    }

    @Override
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

        asSet.addBoolean( CUSTOMIZED, page.isCustomized() );
    }

    @Override
    public Page fromData( final PropertySet asData )
    {
        final Page.Builder page = Page.create();
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
            final PageRegions.Builder pageRegionsBuilder = PageRegions.create();
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

        if ( asData.isNotNull( CUSTOMIZED ) )
        {
            page.customized( asData.getBoolean( CUSTOMIZED ) );
        }

        return page.build();
    }

}
