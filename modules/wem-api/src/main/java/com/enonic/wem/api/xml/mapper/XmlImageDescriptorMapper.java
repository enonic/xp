package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.xml.model.XmlImageDescriptor;

public class XmlImageDescriptorMapper
{

    public static XmlImageDescriptor toXml( final ImageDescriptor object )
    {
        final XmlImageDescriptor result = new XmlImageDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( XmlFormMapper.toXml( object.getConfig() ) );
        return result;
    }

    public static void fromXml( final XmlImageDescriptor xml, final ImageDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( XmlFormMapper.fromXml( xml.getConfig() ) );
    }
}
