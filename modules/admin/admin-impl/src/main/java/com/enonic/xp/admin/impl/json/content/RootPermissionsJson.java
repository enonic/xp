package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.admin.impl.rest.resource.content.json.AccessControlEntriesJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.AccessControlEntryJson;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.acl.AccessControlList;

@SuppressWarnings("UnusedDeclaration")
public class RootPermissionsJson
{
    private final AccessControlEntriesJson accessControlList;

    public RootPermissionsJson( final AccessControlList contentPermissions, final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        final Principals principals = contentPrincipalsResolver.resolveAccessControlListPrincipals( contentPermissions );
        this.accessControlList = AccessControlEntriesJson.from( contentPermissions, principals );
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return this.accessControlList.getList();
    }

}
