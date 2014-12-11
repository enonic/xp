package com.enonic.wem.admin.rest.resource.security.json;

import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;

public final class UserStoreAccessControlEntryJson
{
    private final UserStoreAccessControlEntry entry;

    public UserStoreAccessControlEntryJson( final UserStoreAccessControlEntry entry )
    {
        this.entry = entry;
    }

    public String getPrincipalKey()
    {
        return this.entry.getPrincipal().toString();
    }

    public String getAccess()
    {
        return entry.getAccess().toString();
    }

}
