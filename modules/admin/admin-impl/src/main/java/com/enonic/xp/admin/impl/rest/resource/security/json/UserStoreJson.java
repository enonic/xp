package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.auth.IdProviderDescriptorMode;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class UserStoreJson
    extends UserStoreSummaryJson
{
    private final IdProviderDescriptorMode idProviderMode;

    private final List<UserStoreAccessControlEntryJson> permissions;

    public UserStoreJson( final UserStore userStore, final IdProviderDescriptorMode idProviderMode,
                          final UserStoreAccessControlList userStoreAccessControlList, final Principals principals )
    {
        super( userStore );
        this.idProviderMode = idProviderMode;
        this.permissions = new ArrayList<>();
        if ( userStoreAccessControlList != null )
        {
            for ( UserStoreAccessControlEntry entry : userStoreAccessControlList )
            {
                final Principal principal = principals.getPrincipal( entry.getPrincipal() );
                if ( principal != null )
                {
                    this.permissions.add( new UserStoreAccessControlEntryJson( entry, principal ) );
                }
            }
        }
    }

    public String getIdProviderMode()
    {
        return idProviderMode == null ? null : idProviderMode.toString();
    }

    public List<UserStoreAccessControlEntryJson> getPermissions()
    {
        return this.permissions;
    }
}
