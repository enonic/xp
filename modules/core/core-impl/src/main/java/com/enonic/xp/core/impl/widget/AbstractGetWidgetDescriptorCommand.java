package com.enonic.xp.core.impl.widget;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.widget.WidgetDescriptor;
import com.enonic.xp.xml.parser.XmlWidgetDescriptorParser;

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
