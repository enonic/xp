package com.enonic.xp.core.impl.content.serializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;
import com.enonic.xp.util.Reference;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.PATH;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.TYPE;
import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.CONFIG;
import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.DESCRIPTOR;
import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.appNameToConfigPropertyName;
import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.getConfigFromData;

final class PageDataSerializer
    extends AbstractDataSetSerializer<Page>
{
    private static final String TEMPLATE = "template";

    private static final String CUSTOMIZED = "customized";

    private final ComponentDataSerializerProvider componentDataSerializerProvider;

    PageDataSerializer()
    {
        this( new ComponentDataSerializerProvider() );
    }

    private PageDataSerializer( final ComponentDataSerializerProvider componentDataSerializerProvider )
    {
        this.componentDataSerializerProvider = componentDataSerializerProvider;
    }

    @Override
    public void toData( final Page page, final PropertySet parent )
    {
        if ( page.isFragment() )
        {
            serializeFragment( page, parent );
        }
        else
        {
            serializePage( page, parent );
        }
    }

    private void serializeFragment( final Page page, final PropertySet parent )
    {
        final Component fragment = page.getFragment();
        componentDataSerializerProvider.getDataSerializer( fragment.getType() ).toData( fragment, parent );
    }

    private void serializePage( final Page page, final PropertySet parent )
    {
        final PropertySet asSet = parent.addSet( COMPONENTS );

        asSet.setString( TYPE, PAGE );
        asSet.setString( PATH, ComponentPath.DIVIDER );

        final PropertySet specialBlockSet = asSet.addSet( PAGE );

        if ( page.hasDescriptor() )
        {
            specialBlockSet.addString( DESCRIPTOR, page.getDescriptor().toString() );
        }

        if ( page.hasTemplate() )
        {
            specialBlockSet.addReference( TEMPLATE, new Reference( NodeId.from( page.getTemplate().getContentId() ) ) );
        }

        if ( page.hasRegions() )
        {
            addRegions( page, parent );
        }

        specialBlockSet.addBoolean( CUSTOMIZED, page.isCustomized() );

        if ( page.hasConfig() )
        {
            final String pageName = appNameToConfigPropertyName( page.getDescriptor().getName() );
            final String appKeyAsString = appNameToConfigPropertyName( page.getDescriptor().getApplicationKey().toString() );
            final PropertySet configSet = specialBlockSet.addSet( CONFIG ).addSet( appKeyAsString );
            configSet.addSet( pageName, page.getConfig().getRoot().copy( asSet.getTree() ) );
        }
    }

    private void addRegions( final Page page, final PropertySet asSet )
    {
        if ( !page.getRegions().isEmpty() )
        {
            for ( Region region : page.getRegions() )
            {
                componentDataSerializerProvider.getRegionDataSerializer().toData( region, asSet );
            }
        }
    }

    public Page fromData( final PropertySet asSet )
    {
        final List<PropertySet> componentsAsData = asSet.getProperties( COMPONENTS )
            .stream()
            .filter( Property::hasNotNullValue )
            .map( Property::getSet )
            .collect( Collectors.toList() );

        if ( componentsAsData.isEmpty() )
        {
            return null;
        }

        if ( !isRootComponent( componentsAsData.get( 0 ) ) )
        {
            componentsAsData.sort( Comparator.comparing( this::getComponentPath ) );
        }

        return fromData( componentsAsData );
    }

    private boolean isRootComponent( final PropertySet componentData )
    {
        return getComponentPath( componentData ).equals( ComponentPath.DIVIDER );
    }

    private String getComponentPath( final PropertySet componentData )
    {
        return componentData.getString( PATH );
    }

    private Page fromData( final List<PropertySet> componentsAsData )
    {
        final PropertySet pageData = componentsAsData.getFirst();
        componentsAsData.removeFirst();

        return fromData( pageData, componentsAsData );
    }

    private Page fromData( final PropertySet pageData, final List<PropertySet> componentsAsData )
    {
        final boolean isFragment = !pageData.getString( TYPE ).equals( PAGE );

        if ( isFragment )
        {
            return fromFragmentData( pageData, componentsAsData );
        }

        return fromPageData( pageData, componentsAsData );
    }

    private Page fromFragmentData( final PropertySet fragmentData, final List<PropertySet> componentsAsData )
    {
        final Page.Builder page = Page.create();

        page.fragment( componentDataSerializerProvider.getRegionDataSerializer().getComponent( fragmentData, componentsAsData ) );

        return page.build();
    }

    private Page fromPageData( final PropertySet pageData, final List<PropertySet> componentsAsData )
    {
        final Page.Builder page = Page.create();

        final PropertySet specialBlockSet = pageData.getSet( PAGE );

        if ( specialBlockSet != null )
        {
            if ( specialBlockSet.isNotNull( DESCRIPTOR ) )
            {
                final DescriptorKey descriptorKey = DescriptorKey.from( specialBlockSet.getString( DESCRIPTOR ) );
                page.descriptor( descriptorKey );
                page.config( getConfigFromData( specialBlockSet, descriptorKey ) );
                page.regions( getPageRegions( componentsAsData ) );
            }

            if ( specialBlockSet.isNotNull( TEMPLATE ) )
            {
                page.template( PageTemplateKey.from( specialBlockSet.getReference( TEMPLATE ).toString() ) );
            }

            if ( specialBlockSet.isNotNull( CUSTOMIZED ) )
            {
                page.customized( specialBlockSet.getBoolean( CUSTOMIZED ) );
            }
        }

        return page.build();
    }

    private Regions getPageRegions( final List<PropertySet> componentsAsData )
    {
        final RegionDescriptors regionDescriptors =
            componentDataSerializerProvider.getRegionDataSerializer().getRegionDescriptorsAtLevel( 1, componentsAsData );

        final Regions.Builder regionsBuilder = Regions.create();

        regionDescriptors.forEach( regionDescriptor -> {
            regionsBuilder.add( componentDataSerializerProvider.getRegionDataSerializer()
                                        .fromData( regionDescriptor, ComponentPath.DIVIDER, componentsAsData ) );
        } );

        return regionsBuilder.build();
    }
}
