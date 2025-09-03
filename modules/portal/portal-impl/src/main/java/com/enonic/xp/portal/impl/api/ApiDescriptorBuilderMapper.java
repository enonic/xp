package com.enonic.xp.portal.impl.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.security.PrincipalKeys;

public abstract class ApiDescriptorBuilderMapper
{
    @JsonCreator
    public static ApiDescriptor.Builder create()
    {
        return ApiDescriptor.create();
    }

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
