package com.enonic.wem.api.xml.mapper;


import com.enonic.wem.api.content.page.region.PartDescriptor;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

public final class XmlPartDescriptorMapper
{

    public static XmlPartDescriptor toXml( final PartDescriptor object )
    {
        final XmlPartDescriptor result = new XmlPartDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( XmlFormMapper.toXml( object.getConfig() ) );
        return result;
    }

    public static void fromXml( final XmlPartDescriptor xml, final PartDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( XmlFormMapper.fromXml( xml.getConfig() ) );
    }

}
