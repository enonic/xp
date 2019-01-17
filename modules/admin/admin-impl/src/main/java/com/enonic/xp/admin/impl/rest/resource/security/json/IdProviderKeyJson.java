package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.IdProviderKey;

@SuppressWarnings("UnusedDeclaration")
public class IdProviderKeyJson
{
    private final IdProviderKey idProviderKey;

    @JsonCreator
    public IdProviderKeyJson( @JsonProperty("id") final String id )
    {

        this.idProviderKey = IdProviderKey.from( id );
    }

    public IdProviderKeyJson( final IdProviderKey idProviderKey )
    {
        this.idProviderKey = idProviderKey;

    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }
}
