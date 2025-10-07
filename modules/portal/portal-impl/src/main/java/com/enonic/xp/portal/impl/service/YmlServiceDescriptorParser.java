package com.enonic.xp.portal.impl.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
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
        return PARSER.parse( resource, ServiceDescriptor.Builder.class, currentApplication );
    }

    private abstract static class ServiceDescriptorBuilderMapper
    {
        @JsonProperty("allow")
        abstract ServiceDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );
    }
}
