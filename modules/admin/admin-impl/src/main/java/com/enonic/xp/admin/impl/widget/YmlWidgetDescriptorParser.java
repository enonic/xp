package com.enonic.xp.admin.impl.widget;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;

public final class YmlWidgetDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( WidgetDescriptor.Builder.class, WidgetDescriptorBuilderMapper.class );
    }

    public static WidgetDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, WidgetDescriptor.Builder.class, currentApplication );
    }

    private abstract static class WidgetDescriptorBuilderMapper
    {
        @JsonProperty("displayName")
        public abstract WidgetDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        public abstract WidgetDescriptor.Builder description( LocalizedText text );

        @JsonProperty("allow")
        public abstract WidgetDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("interfaces")
        public abstract WidgetDescriptor.Builder addInterfaces( Iterable<String> interfaceNames );

        @JsonProperty("config")
        public abstract WidgetDescriptor.Builder addProperty( String key, String value );
    }
}
