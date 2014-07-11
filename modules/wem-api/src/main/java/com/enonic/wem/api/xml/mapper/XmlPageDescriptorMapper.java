package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.model.XmlRegionDescriptor;
import com.enonic.wem.api.xml.model.XmlRegionDescriptors;

public class XmlPageDescriptorMapper
{

    public static XmlPageDescriptor toXml( final PageDescriptor object )
    {
        final XmlPageDescriptor result = new XmlPageDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( XmlFormMapper.toXml( object.getConfig() ) );
        result.setRegions( toXml( object.getRegions() ) );
        return result;
    }

    public static void fromXml( final XmlPageDescriptor xml, final PageDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( XmlFormMapper.fromXml( xml.getConfig() ) );
        builder.regions( fromXml( xml.getRegions() ) );
    }

    private static RegionDescriptors fromXml( final XmlRegionDescriptors regions )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.newRegionDescriptors();
        for ( XmlRegionDescriptor descriptor : regions.getList() )
        {
            builder.add( fromXml( descriptor ) );
        }
        return builder.build();
    }

    private static RegionDescriptor fromXml( final XmlRegionDescriptor descriptor )
    {
        final RegionDescriptor.Builder builder = RegionDescriptor.newRegionDescriptor();
        builder.name( descriptor.getName() );
        return builder.build();
    }

    private static XmlRegionDescriptors toXml( final RegionDescriptors object )
    {
        final XmlRegionDescriptors result = new XmlRegionDescriptors();
        for ( final RegionDescriptor descriptor : object )
        {
            result.getList().add( toXml( descriptor ) );
        }
        return result;
    }

    private static XmlRegionDescriptor toXml( final RegionDescriptor object )
    {
        final XmlRegionDescriptor result = new XmlRegionDescriptor();
        result.setName( object.getName() );
        return result;
    }
}
