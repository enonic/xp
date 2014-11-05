package com.enonic.wem.admin.rest.resource.security.json;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.User;

public class PrincipalJson
{
    private final Principal principal;

    @JsonCreator
    public PrincipalJson( @JsonProperty("displayName") final String displayName, @JsonProperty("key") final String pKey )
    {
        PrincipalKey principalKey = PrincipalKey.from( pKey );
        switch ( principalKey.getType() )
        {
            case GROUP:
            {
                this.principal = Group.create().displayName( displayName ).key( principalKey ).build();
                break;
            }
            case USER:
            {
                this.principal = User.create().displayName( displayName ).key( principalKey ).build();
                break;
            }
            case ROLE:
            {
                this.principal = Role.create().displayName( displayName ).key( principalKey ).build();
                break;
            }
            default:
            {
                principal = null;
            }
        }

    }

    public PrincipalJson( final Principal principal )
    {
        this.principal = principal;

    }

    public String getKey()
    {
        return principal.getKey().toString();
    }

    public String getDisplayName()
    {
        return principal.getDisplayName();
    }

    public Instant getModifiedTime()
    {
        return principal.getModifiedTime();
    }
}
