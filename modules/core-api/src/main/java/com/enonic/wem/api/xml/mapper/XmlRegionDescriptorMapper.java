package com.enonic.wem.api.xml.mapper;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.xml.model.XmlRegionDescriptor;

final class XmlRegionDescriptorMapper
{
    protected static RegionDescriptors fromXml( final List<XmlRegionDescriptor> regions )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.newRegionDescriptors();

        if ( regions != null )
        {
            for ( final XmlRegionDescriptor descriptor : regions )
            {
                builder.add( fromXml( descriptor ) );
            }
        }

        return builder.build();
    }

    protected static RegionDescriptor fromXml( final XmlRegionDescriptor descriptor )
    {
        final RegionDescriptor.Builder builder = RegionDescriptor.newRegionDescriptor();
        builder.name( descriptor.getName() );
        return builder.build();
    }

    protected static List<XmlRegionDescriptor> toXml( final RegionDescriptors object )
    {
        final List<XmlRegionDescriptor> result = Lists.newArrayList();
        for ( final RegionDescriptor descriptor : object )
        {
            result.add( toXml( descriptor ) );
        }
        return result;
    }

    protected static XmlRegionDescriptor toXml( final RegionDescriptor object )
    {
        final XmlRegionDescriptor result = new XmlRegionDescriptor();
        result.setName( object.getName() );
        return result;
    }
}
