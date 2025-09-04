package com.enonic.xp.core.impl.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;

abstract class ServiceDescriptorBuilderMapper
{
    @JsonCreator
    static ServiceDescriptor.Builder create()
    {
        return ServiceDescriptor.create();
    }

    @JsonProperty("allow")
    abstract ServiceDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );
}
