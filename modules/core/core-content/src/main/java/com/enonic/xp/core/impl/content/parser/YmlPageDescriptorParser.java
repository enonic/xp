package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.LocalizedText;

public final class YmlPageDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( PageDescriptor.Builder.class, PageDescriptorBuilderMixIn.class );
    }

    public static PageDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, PageDescriptor.Builder.class, currentApplication );
    }

    private abstract static class PageDescriptorBuilderMixIn
    {
        @JsonCreator
        static PageDescriptor.Builder create()
        {
            return PageDescriptor.create();
        }

        @JsonProperty("displayName")
        abstract PageDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract PageDescriptor.Builder description( LocalizedText text );

        @JsonProperty("form")
        abstract PageDescriptor.Builder config( Form config );

        @JsonProperty("regions")
        @JsonDeserialize(using = RegionDescriptorsDeserializer.class)
        abstract PageDescriptor.Builder regions( RegionDescriptors value );

        @JsonProperty("config")
        @JsonDeserialize(using = InputTypeConfigDeserializer.class)
        abstract PageDescriptor.Builder schemaConfig( InputTypeConfig value );
    }
}
