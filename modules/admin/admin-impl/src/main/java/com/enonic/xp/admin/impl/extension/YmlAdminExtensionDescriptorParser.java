package com.enonic.xp.admin.impl.extension;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.util.GenericValue;

public final class YmlAdminExtensionDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( AdminExtensionDescriptor.Builder.class, AdminExtensionDescriptorBuilderMapper.class );
    }

    public static AdminExtensionDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "AdminExtension", resource, AdminExtensionDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class AdminExtensionDescriptorBuilderMapper
    {
        @JsonProperty("title")
        abstract AdminExtensionDescriptor.Builder title( LocalizedText text );

        @JsonProperty("description")
        abstract AdminExtensionDescriptor.Builder description( LocalizedText text );

        @JsonProperty("allow")
        abstract AdminExtensionDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("interfaces")
        abstract AdminExtensionDescriptor.Builder interfaces( String... interfaceNames );

        @JsonProperty("config")
        abstract AdminExtensionDescriptor.Builder schemaConfig( GenericValue value );
    }
}
