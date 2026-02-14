package com.enonic.xp.portal.impl.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.security.PrincipalKeys;

public final class YmlApiDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ApiDescriptor.Builder.class, ApiDescriptorBuilderMapper.class );
    }

    public static ApiDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, ApiDescriptor.Builder.class, currentApplication );
    }

    private abstract static class ApiDescriptorBuilderMapper
    {
        @JsonProperty("allow")
        public abstract ApiDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("displayName")
        public abstract ApiDescriptor.Builder displayName( String displayName );

        @JsonProperty("documentationUrl")
        public abstract ApiDescriptor.Builder documentationUrl( String documentationUrl );

        @JsonProperty("description")
        public abstract ApiDescriptor.Builder description( String description );

        @JsonProperty("mount")
        abstract ApiDescriptor.Builder mount( Boolean value );
    }
}
