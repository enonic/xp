package com.enonic.xp.portal.impl.webapp;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKeys;
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

    private abstract static class WebappDescriptorMapper
    {
        @JsonProperty("apis")
        abstract WebappDescriptor.Builder apiMounts( DescriptorKeys apiMounts );

        @JacksonInject("currentApplication")
        abstract WebappDescriptor.Builder applicationKey( ApplicationKey applicationKey );
    }
}
