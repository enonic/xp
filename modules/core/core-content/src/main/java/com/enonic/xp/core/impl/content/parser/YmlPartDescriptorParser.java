package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.LocalizedText;

public final class YmlPartDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( PartDescriptor.Builder.class, PartDescriptorBuilderMixIn.class );
    }

    public static PartDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, PartDescriptor.Builder.class, currentApplication );
    }

    private abstract static class PartDescriptorBuilderMixIn
    {
        @JsonCreator
        static PartDescriptor.Builder create()
        {
            return PartDescriptor.create();
        }

        @JsonProperty("displayName")
        abstract PartDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract PartDescriptor.Builder description( LocalizedText text );

        @JsonProperty("form")
        abstract PartDescriptor.Builder config( Form config );

        @JsonProperty("config")
        @JsonDeserialize(using = InputTypeConfigDeserializer.class)
        abstract PartDescriptor.Builder schemaConfig( InputTypeConfig value );
    }
}
