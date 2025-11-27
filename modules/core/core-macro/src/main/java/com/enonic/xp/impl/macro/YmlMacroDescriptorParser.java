package com.enonic.xp.impl.macro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.schema.LocalizedText;

final class YmlMacroDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( MacroDescriptor.Builder.class, MacroDescriptorBuilderMixIn.class );
    }

    static MacroDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, MacroDescriptor.Builder.class, currentApplication );
    }

    private abstract static class MacroDescriptorBuilderMixIn
    {
        @JsonCreator
        static MacroDescriptor.Builder create()
        {
            return MacroDescriptor.create();
        }

        @JsonProperty("displayName")
        abstract MacroDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract MacroDescriptor.Builder description( LocalizedText text );

        @JsonProperty("form")
        abstract MacroDescriptor.Builder form( Form form );
    }
}
