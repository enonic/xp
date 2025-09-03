package com.enonic.xp.portal.impl.api;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.core.impl.schema.YmlParserBase;

public final class YmlApiDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ApiDescriptor.Builder.class, ApiDescriptorBuilderMapper.class );
    }

    public static ApiDescriptor.Builder parse( final String resource )
    {
        return PARSER.parse( resource, ApiDescriptor.Builder.class );
    }
}
