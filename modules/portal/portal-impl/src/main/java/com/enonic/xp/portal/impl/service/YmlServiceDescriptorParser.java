package com.enonic.xp.portal.impl.service;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.service.ServiceDescriptor;

final class YmlServiceDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ServiceDescriptor.Builder.class, ServiceDescriptorBuilderMapper.class );
    }

    static ServiceDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, ServiceDescriptor.Builder.class, currentApplication );
    }
}
