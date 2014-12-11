package com.enonic.wem.admin.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

@SuppressWarnings("UnusedDeclaration")
public class UserStoreJson
    extends UserStoreSummaryJson
{
    private final UserStoreAccessControlList userStoreAccessControlList;

    public UserStoreJson( final UserStore userStore, final UserStoreAccessControlList userStoreAccessControlList )
    {
        super( userStore );
        this.userStoreAccessControlList = userStoreAccessControlList;
    }

    public List<UserStoreAccessControlEntryJson> getPermissions()
    {
        final List<UserStoreAccessControlEntryJson> list = new ArrayList<>();
        for ( UserStoreAccessControlEntry entry : userStoreAccessControlList )
        {
            list.add( new UserStoreAccessControlEntryJson( entry ) );
        }
        return list;
    }
}
