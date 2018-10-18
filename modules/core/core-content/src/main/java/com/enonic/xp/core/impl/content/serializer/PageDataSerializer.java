package com.enonic.xp.core.impl.content.serializer;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
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
            addRegions( page, asSet );
        }

        if ( page.hasConfig() )
        {
            asSet.addSet( CONFIG, page.getConfig().getRoot().copy( asSet.getTree() ) );
        }

        asSet.addBoolean( CUSTOMIZED, page.isCustomized() );

        if ( page.isFragment() )
        {
            addFragmentComponent( page, asSet );
        }
    }

    private void addRegions( final Page page, final PropertySet asSet )
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
            asSet.addSet( ComponentDataSerializer.COMPONENTS, null );
        }
    }

    private void addFragmentComponent( final Page page, final PropertySet asSet )
    {
        final PropertySet fragmentAsData = new PropertySet();
        asSet.addSet( FRAGMENT, fragmentAsData );

        final Component fragment = page.getFragment();
        fragmentAsData.setString( TYPE, fragment.getType().getComponentClass().getSimpleName() );
        final ComponentDataSerializer serializer = componentDataSerializerProvider.getDataSerializer( fragment.getType() );
        serializer.toData( fragment, fragmentAsData );
    }

    @Override
    public Page fromData( final SerializedData data )
    {
        final PropertySet asData = data.getAsData();
        final Page.Builder page = Page.create();

        if ( asData.isNotNull( CONTROLLER ) )
        {
            page.controller( DescriptorKey.from( asData.getString( CONTROLLER ) ) );
        }

        if ( asData.isNotNull( TEMPLATE ) )
        {
            page.template( PageTemplateKey.from( asData.getReference( TEMPLATE ).toString() ) );
        }

        if ( asData.hasProperty( ComponentDataSerializer.COMPONENTS ) )
        {
            page.regions( getPageRegions( asData ) );
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
            page.fragment( getFragmentComponent( asData ) );
        }

        return page.build();
    }

    private PageRegions getPageRegions( final PropertySet asData )
    {
        final PageRegions.Builder pageRegionsBuilder = PageRegions.create();

        final List<PropertySet> componentsAsData =
            asData.getProperties( ComponentDataSerializer.COMPONENTS ).stream().filter( Property::hasNotNullValue ).map(
                item -> item.getSet() ).collect( Collectors.toList() );

        componentsAsData.stream().filter( this::isTopLevelRegion ).forEach( regionAsData -> {
            pageRegionsBuilder.add( regionDataSerializer.fromData( new SerializedData( regionAsData, componentsAsData ) ) );
        } );

        return pageRegionsBuilder.build();
    }

    private Component getFragmentComponent( final PropertySet asData )
    {
        final PropertySet fragmentAsProperty = asData.getPropertySet( FRAGMENT );
        final ComponentType type = ComponentTypes.bySimpleClassName( fragmentAsProperty.getString( TYPE ) );
        final List<PropertySet> componentsAsData =
            fragmentAsProperty.getProperties( ComponentDataSerializer.COMPONENTS ).stream().filter( Property::hasNotNullValue ).map(
                item -> item.getSet() ).collect( Collectors.toList() );

        return componentDataSerializerProvider.getDataSerializer( type ).fromData(
            new SerializedData( componentsAsData.get( 0 ), componentsAsData ) );
    }

    private boolean isTopLevelRegion( final PropertySet asData )
    {
        return asData.getString( ComponentDataSerializer.TYPE ).equals( Region.class.getSimpleName() ) &&
            !asData.getString( ComponentDataSerializer.PATH ).contains( "/" );
    }
}
