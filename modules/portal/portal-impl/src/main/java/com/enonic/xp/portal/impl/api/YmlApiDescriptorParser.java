package com.enonic.xp.portal.impl.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.util.GenericValue;

public final class YmlApiDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ApiDescriptor.Builder.class, ApiDescriptorBuilderMapper.class );
    }

    public static ApiDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "API", resource, ApiDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class ApiDescriptorBuilderMapper
    {
        @JsonProperty("allow")
        abstract ApiDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("title")
        abstract ApiDescriptor.Builder title( LocalizedText text );

        @JsonProperty("documentationUrl")
        abstract ApiDescriptor.Builder documentationUrl( String documentationUrl );

        @JsonProperty("description")
        abstract ApiDescriptor.Builder description( LocalizedText text );

        @JsonProperty("mount")
        abstract ApiDescriptor.Builder mount( String... value );

        @JsonProperty("config")
        abstract ApiDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }
}
