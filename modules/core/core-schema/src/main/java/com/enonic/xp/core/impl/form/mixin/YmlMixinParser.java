package com.enonic.xp.core.impl.form.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.mixin.Mixin;

public final class YmlMixinParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( Mixin.Builder.class, MixinBuilderMixIn.class );
    }

    static Mixin.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, Mixin.Builder.class, currentApplication );
    }

    private abstract static class MixinBuilderMixIn
    {
        @JsonCreator
        static Mixin.Builder create()
        {
            return Mixin.create();
        }

        @JsonProperty("form")
        abstract Mixin.Builder form( Form value );

        @JsonProperty("displayName")
        abstract Mixin.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract Mixin.Builder description( LocalizedText text );
    }
}
