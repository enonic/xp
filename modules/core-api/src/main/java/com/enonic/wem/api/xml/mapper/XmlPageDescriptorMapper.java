package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;

public final class XmlPageDescriptorMapper
{
    public static XmlPageDescriptor toXml( final PageDescriptor object )
    {
        final XmlPageDescriptor result = new XmlPageDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( XmlFormMapper.toXml( object.getConfig() ) );
        result.setRegions( XmlRegionDescriptorMapper.toXml( object.getRegions() ) );
        return result;
    }

    public static void fromXml( final XmlPageDescriptor xml, final PageDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( XmlFormMapper.fromXml( xml.getConfig() ) );
        builder.regions( XmlRegionDescriptorMapper.fromXml( xml.getRegions() ) );
    }
}
