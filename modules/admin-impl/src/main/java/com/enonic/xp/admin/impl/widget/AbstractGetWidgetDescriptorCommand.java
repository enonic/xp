package com.enonic.xp.admin.impl.widget;

import com.enonic.xp.admin.impl.xml.parser.XmlWidgetDescriptorParser;
import com.enonic.xp.app.ApplicationKey;

abstract class AbstractGetWidgetDescriptorCommand<T extends AbstractGetWidgetDescriptorCommand>
{

    protected void parseXml( final ApplicationKey applicationKey, final WidgetDescriptor.Builder builder, final String xml )
    {
        final XmlWidgetDescriptorParser parser = new XmlWidgetDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }
}
