package com.enonic.xp.admin.impl.widget;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;

public final class YmlWidgetDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( WidgetDescriptor.Builder.class, WidgetDescriptorBuilderMapper.class );
    }

    public static WidgetDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, WidgetDescriptor.Builder.class, currentApplication );
    }
}
