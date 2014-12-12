package com.enonic.wem.admin.rest.resource.security.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.UserStoreAccess;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;

public final class UserStoreAccessControlEntryJson
{
    private final UserStoreAccessControlEntry entry;

    public UserStoreAccessControlEntryJson( final UserStoreAccessControlEntry entry )
    {
        this.entry = entry;
    }

    @JsonCreator
    public UserStoreAccessControlEntryJson( @JsonProperty("principalKey") final String principalKey,
                                            @JsonProperty("access") final String access )
    {
        this.entry = UserStoreAccessControlEntry.create().principal( PrincipalKey.from( principalKey ) ).access(
            UserStoreAccess.valueOf( access.toUpperCase() ) ).build();
    }

    public String getPrincipalKey()
    {
        return this.entry.getPrincipal().toString();
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
