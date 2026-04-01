package com.enonic.xp.portal.impl.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;

final class YmlServiceDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ServiceDescriptor.Builder.class, ServiceDescriptorBuilderMapper.class );
    }

    static ServiceDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "Service", resource, ServiceDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class ServiceDescriptorBuilderMapper
    {
        @JsonProperty("allow")
        abstract ServiceDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("title")
        abstract ServiceDescriptor.Builder title( LocalizedText text );
    }
}
