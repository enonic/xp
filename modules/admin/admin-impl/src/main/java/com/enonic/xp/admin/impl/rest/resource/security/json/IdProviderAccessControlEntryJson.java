package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.IdProviderAccess;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;

@SuppressWarnings("UnusedDeclaration")
public final class IdProviderAccessControlEntryJson
{
    private final IdProviderAccessControlEntry entry;

    private final PrincipalJson principalJson;

    public IdProviderAccessControlEntryJson( final IdProviderAccessControlEntry entry, final Principal principal )
    {
        this.entry = entry;
        this.principalJson = new PrincipalJson( principal );
    }

    @JsonCreator
    public IdProviderAccessControlEntryJson( @JsonProperty("principal") final PrincipalJson principal,
                                             @JsonProperty("access") final String access )
    {
        Objects.requireNonNull( principal, "Missing principal parameter" );
        this.entry = IdProviderAccessControlEntry.create().principal( PrincipalKey.from( principal.getKey() ) ).access(
            IdProviderAccess.valueOf( access.toUpperCase() ) ).build();
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
    public IdProviderAccessControlEntry getEntry()
    {
        return entry;
    }
}
