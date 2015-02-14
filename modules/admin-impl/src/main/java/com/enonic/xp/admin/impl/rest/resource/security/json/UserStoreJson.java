package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class UserStoreJson
    extends UserStoreSummaryJson
{
    private final List<UserStoreAccessControlEntryJson> permissions;

    public UserStoreJson( final UserStore userStore, final UserStoreAccessControlList userStoreAccessControlList,
                          final Principals principals )
    {
        super( userStore );
        this.permissions = new ArrayList<>();
        for ( UserStoreAccessControlEntry entry : userStoreAccessControlList )
        {
            final Principal principal = principals.getPrincipal( entry.getPrincipal() );
            if ( principal != null )
            {
                this.permissions.add( new UserStoreAccessControlEntryJson( entry, principal ) );
            }
        }
    }

    public List<UserStoreAccessControlEntryJson> getPermissions()
    {
        return this.permissions;
    }
}
