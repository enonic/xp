package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.xml.model.XmlRegionDescriptor;
import com.enonic.wem.api.xml.model.XmlRegionsDescriptor;

final class XmlRegionDescriptorMapper
{
    protected static RegionDescriptors fromXml( final XmlRegionsDescriptor regions )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.newRegionDescriptors();

        if ( regions != null )
        {
            for ( final XmlRegionDescriptor descriptor : regions.getList() )
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

    protected static XmlRegionsDescriptor toXml( final RegionDescriptors object )
    {
        final XmlRegionsDescriptor result = new XmlRegionsDescriptor();
        for ( final RegionDescriptor descriptor : object )
        {
            result.getList().add( toXml( descriptor ) );
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
