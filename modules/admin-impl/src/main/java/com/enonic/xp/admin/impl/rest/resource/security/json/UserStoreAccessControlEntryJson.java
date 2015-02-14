package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.security.Principal;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.acl.UserStoreAccess;
import com.enonic.xp.core.security.acl.UserStoreAccessControlEntry;

@SuppressWarnings("UnusedDeclaration")
public final class UserStoreAccessControlEntryJson
{
    private final UserStoreAccessControlEntry entry;

    private final PrincipalJson principalJson;

    public UserStoreAccessControlEntryJson( final UserStoreAccessControlEntry entry, final Principal principal )
    {
        this.entry = entry;
        this.principalJson = new PrincipalJson( principal );
    }

    @JsonCreator
    public UserStoreAccessControlEntryJson( @JsonProperty("principal") final PrincipalJson principal,
                                            @JsonProperty("access") final String access )
    {
        Objects.requireNonNull( principal, "Missing principal parameter" );
        this.entry = UserStoreAccessControlEntry.create().principal( PrincipalKey.from( principal.getKey() ) ).access(
            UserStoreAccess.valueOf( access.toUpperCase() ) ).build();
        this.principalJson = null;
    }

    public PrincipalJson getPrincipal()
    {
        return this.principalJson;
    }

    public String getAccess()
    {
        return entry.getAccess().toString();
    }

    @JsonIgnore
    public UserStoreAccessControlEntry getEntry()
    {
        return entry;
    }
}
