package com.enonic.xp.xml.parser;

import org.w3c.dom.Element;

import com.enonic.xp.content.page.region.RegionDescriptor;
import com.enonic.xp.content.page.region.RegionDescriptors;
import com.enonic.xp.xml.DomHelper;

final class XmlRegionDescriptorsMapper
{
    public RegionDescriptors buildRegions( final Element root )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.newRegionDescriptors();
        if ( root != null )
        {
            buildRegions( builder, root );
        }

        return builder.build();
    }

    private void buildRegions( final RegionDescriptors.Builder builder, final Element root )
    {
        for ( final Element child : DomHelper.getChildElementsByTagName( root, "region" ) )
        {
            builder.add( buildRegion( child ) );
        }
    }

    private RegionDescriptor buildRegion( final Element root )
    {
        final RegionDescriptor.Builder builder = RegionDescriptor.newRegionDescriptor();
        builder.name( XmlParserHelper.getAttributeAsString( root, "name", null ) );
        return builder.build();
    }
}
