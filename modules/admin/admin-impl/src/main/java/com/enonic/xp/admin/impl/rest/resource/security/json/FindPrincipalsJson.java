package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PrincipalType;

public final class FindPrincipalsJson
{
    @JsonProperty("types")
    public List<String> types = Collections.emptyList();

    @JsonProperty("query")
    public String query;

    @JsonProperty("memberships")
    public boolean resolveMemberships;

    @JsonProperty("userStoreKey")
    public String storeKey;

    @JsonProperty("from")
    public Integer from;

    @JsonProperty("size")
    public Integer size;

    @JsonIgnore
    public List<PrincipalType> toPrincipalTypes()
    {
        final List<PrincipalType> principalTypes = new ArrayList<>();

        for ( String typeItem : types )
        {
            try
            {
                principalTypes.add( PrincipalType.valueOf( typeItem.toUpperCase() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new WebApplicationException( "Invalid principal type: " + typeItem );
            }
        }

        return principalTypes;
    }
}
