package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.xdata.XData;

public final class YmlXDataParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( XData.Builder.class, XDataBuilderMixIn.class );
    }

    public static XData.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, XData.Builder.class, currentApplication );
    }

    private abstract static class XDataBuilderMixIn
    {
        @JsonCreator
        static XData.Builder create()
        {
            return XData.create();
        }

        @JsonProperty("form")
        abstract XData.Builder form( Form value );

        @JsonProperty("displayName")
        abstract XData.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract XData.Builder description( LocalizedText text );
    }
}
