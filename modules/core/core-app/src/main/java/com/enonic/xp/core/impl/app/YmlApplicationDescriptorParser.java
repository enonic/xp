package com.enonic.xp.core.impl.app;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;

final class YmlApplicationDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ApplicationDescriptor.Builder.class, ApplicationDescriptorBuilderMapper.class );
    }

    static ApplicationDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, ApplicationDescriptor.Builder.class, currentApplication );
    }

    private abstract static class ApplicationDescriptorBuilderMapper
    {
        @JsonProperty("description")
        abstract ApplicationDescriptor.Builder description( String description );

        @JacksonInject("currentApplication")
        abstract ApplicationDescriptor.Builder key( ApplicationKey key );
    }
}
