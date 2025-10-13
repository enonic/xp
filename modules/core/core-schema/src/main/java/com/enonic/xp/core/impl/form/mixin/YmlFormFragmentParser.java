package com.enonic.xp.core.impl.form.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.mixin.FormFragmentDescriptor;

public final class YmlFormFragmentParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( FormFragmentDescriptor.Builder.class, FormFragmentBuilderMixIn.class );
    }

    public static FormFragmentDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, FormFragmentDescriptor.Builder.class, currentApplication );
    }

    private abstract static class FormFragmentBuilderMixIn
    {
        @JsonCreator
        static FormFragmentDescriptor.Builder create()
        {
            return FormFragmentDescriptor.create();
        }

        @JsonProperty("form")
        abstract FormFragmentDescriptor.Builder form( Form value );

        @JsonProperty("displayName")
        abstract FormFragmentDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract FormFragmentDescriptor.Builder description( LocalizedText text );
    }
}
