package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.region.LayoutDescriptor;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;

public final class XmlLayoutDescriptorMapper
{

    public static XmlLayoutDescriptor toXml( final LayoutDescriptor object )
    {
        final XmlLayoutDescriptor result = new XmlLayoutDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( XmlFormMapper.toXml( object.getConfig() ) );
        result.setRegions( XmlRegionDescriptorMapper.toXml( object.getRegions() ) );
        return result;
    }

    public static void fromXml( final XmlLayoutDescriptor xml, final LayoutDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( XmlFormMapper.fromXml( xml.getConfig() ) );
        builder.regions( XmlRegionDescriptorMapper.fromXml( xml.getRegions() ) );
    }

}
