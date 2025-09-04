package com.enonic.xp.portal.impl.webapp;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.webapp.WebappDescriptor;

public class YmlWebappDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( WebappDescriptor.Builder.class, WebappDescriptorMapper.class );
    }

    static WebappDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, WebappDescriptor.Builder.class, currentApplication );
    }
}
