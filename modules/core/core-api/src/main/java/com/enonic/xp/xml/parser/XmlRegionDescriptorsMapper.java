package com.enonic.xp.xml.parser;

import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.xml.DomElement;

final class XmlRegionDescriptorsMapper
{
    public RegionDescriptors buildRegions( final DomElement root )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.create();
        if ( root != null )
        {
            buildRegions( builder, root );
        }

        return builder.build();
    }

    private void buildRegions( final RegionDescriptors.Builder builder, final DomElement root )
    {
        for ( final DomElement child : root.getChildren( "region" ) )
        {
            builder.add( buildRegion( child ) );
        }
    }

    private RegionDescriptor buildRegion( final DomElement root )
    {
        final RegionDescriptor.Builder builder = RegionDescriptor.create();
        builder.name( root.getAttribute( "name" ) );
        return builder.build();
    }
}
