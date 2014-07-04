package com.enonic.wem.core.module;

import com.enonic.wem.api.xml.mapper.XmlFormMapper;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlVendor;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;

final class ModuleXmlBuilder
{
    public void toModule( final String xml, final ModuleBuilder builder )
    {
        final XmlModule object = XmlSerializers2.module().parse( xml );
        toModule( object, builder );
    }

    private void toModule( final XmlModule xml, final ModuleBuilder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.url( xml.getUrl() );

        final XmlVendor vendor = xml.getVendor();
        if ( vendor != null )
        {
            builder.vendorUrl( vendor.getUrl() );
            builder.vendorName( vendor.getName() );
        }

        final XmlForm config = xml.getConfig();
        if ( config != null )
        {
            builder.config( XmlFormMapper.fromXml( config ) );
        }
    }
}
