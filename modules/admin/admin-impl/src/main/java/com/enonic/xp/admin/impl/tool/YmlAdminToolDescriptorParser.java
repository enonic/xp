package com.enonic.xp.admin.impl.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.util.GenericValue;

public final class YmlAdminToolDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( AdminToolDescriptor.Builder.class, AdminToolDescriptorBuilderMapper.class );
    }

    public static AdminToolDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "AdminTool", resource, AdminToolDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class AdminToolDescriptorBuilderMapper
    {
        @JsonProperty("title")
        abstract AdminToolDescriptor.Builder title( LocalizedText text );

        @JsonProperty("description")
        abstract AdminToolDescriptor.Builder description( LocalizedText text );

        @JsonProperty("allow")
        abstract AdminToolDescriptor.Builder addAllowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("apis")
        abstract AdminToolDescriptor.Builder apiMounts( DescriptorKeys apiDescriptors );

        @JsonProperty("interfaces")
        abstract AdminToolDescriptor.Builder interfaces( String... interfaces );

        @JsonProperty("config")
        abstract AdminToolDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }
}
