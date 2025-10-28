package com.enonic.xp.admin.impl.extension;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;

public final class YmlAdminExtensionDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( AdminExtensionDescriptor.Builder.class, AdminExtensionDescriptorBuilderMapper.class );
    }

    public static AdminExtensionDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, AdminExtensionDescriptor.Builder.class, currentApplication );
    }

    private abstract static class AdminExtensionDescriptorBuilderMapper
    {
        @JsonProperty("displayName")
        public abstract AdminExtensionDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        public abstract AdminExtensionDescriptor.Builder description( LocalizedText text );

        @JsonProperty("allow")
        public abstract AdminExtensionDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("interfaces")
        public abstract AdminExtensionDescriptor.Builder addInterfaces( Iterable<String> interfaceNames );

        @JsonProperty("config")
        public abstract AdminExtensionDescriptor.Builder addProperty( String key, String value );
    }
}
