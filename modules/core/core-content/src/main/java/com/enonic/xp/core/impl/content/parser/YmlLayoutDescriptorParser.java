package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.GenericValue;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.LocalizedText;

public final class YmlLayoutDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( LayoutDescriptor.Builder.class, LayoutDescriptorBuilderMixIn.class );
    }

    public static LayoutDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, LayoutDescriptor.Builder.class, currentApplication );
    }

    private abstract static class LayoutDescriptorBuilderMixIn
    {
        @JsonProperty("displayName")
        abstract LayoutDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract LayoutDescriptor.Builder description( LocalizedText text );

        @JsonProperty("form")
        abstract LayoutDescriptor.Builder config( Form config );

        @JsonProperty("regions")
        @JsonDeserialize(using = RegionDescriptorsDeserializer.class)
        abstract LayoutDescriptor.Builder regions( RegionDescriptors value );

        @JsonProperty("config")
        abstract LayoutDescriptor.Builder schemaConfig( GenericValue value );
    }
}
