package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.Region;
import com.enonic.xp.util.Reference;

import static com.enonic.xp.core.impl.content.serializer.ComponentsDataSerializer.TYPE;

public final class PageDataSerializer
    extends AbstractDataSetSerializer<Page, Page>
{
    private static final String CONTROLLER = "controller";

    private static final String TEMPLATE = "template";

    private static final String CONFIG = "config";

    private static final String REGION = "region";

    private static final String CUSTOMIZED = "customized";

    private static final String FRAGMENT = "fragment";

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    private final ComponentDataSerializerProvider componentDataSerializerProvider = new ComponentDataSerializerProvider();

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

        if ( page.isFragment() )
        {
            final PropertySet fragmentAsData = new PropertySet();
            asSet.addSet( FRAGMENT, fragmentAsData );

            final Component fragment = page.getFragment();
            fragmentAsData.setString( TYPE, fragment.getType().getComponentClass().getSimpleName() );
            final ComponentDataSerializer serializer = componentDataSerializerProvider.getDataSerializer( fragment.getType() );
            serializer.toData( fragment, fragmentAsData );
        }
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

        if ( asData.isNotNull( FRAGMENT ) )
        {
            final PropertySet fragmentAsProperty = asData.getPropertySet( FRAGMENT );
            final ComponentType type = ComponentTypes.bySimpleClassName( fragmentAsProperty.getString( TYPE ) );
            final PropertySet componentSet = fragmentAsProperty.getSet( type.getComponentClass().getSimpleName() );
            final Component component = componentDataSerializerProvider.getDataSerializer( type ).fromData( componentSet );
            page.fragment( component );
        }

        return page.build();
    }

}
