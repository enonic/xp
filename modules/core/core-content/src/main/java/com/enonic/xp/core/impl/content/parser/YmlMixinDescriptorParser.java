package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.util.GenericValue;

public final class YmlMixinDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( MixinDescriptor.Builder.class, MixinDescriptorBuilderMixIn.class );
    }

    public static MixinDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "Mixin", resource, MixinDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class MixinDescriptorBuilderMixIn
    {
        @JsonProperty("form")
        abstract MixinDescriptor.Builder form( Form value );

        @JsonProperty("title")
        abstract MixinDescriptor.Builder title( LocalizedText text );

        @JsonProperty("description")
        abstract MixinDescriptor.Builder description( LocalizedText text );

        @JsonProperty("config")
        abstract MixinDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }
}
