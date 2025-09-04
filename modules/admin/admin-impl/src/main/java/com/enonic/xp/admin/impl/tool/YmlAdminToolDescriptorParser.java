package com.enonic.xp.admin.impl.tool;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;

public final class YmlAdminToolDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( AdminToolDescriptor.Builder.class, AdminToolDescriptorBuilderMapper.class );
    }

    public static AdminToolDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, AdminToolDescriptor.Builder.class, currentApplication );
    }
}
