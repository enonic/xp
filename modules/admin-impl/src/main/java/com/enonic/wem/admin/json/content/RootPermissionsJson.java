package com.enonic.wem.admin.json.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.admin.rest.resource.content.json.AccessControlEntryJson;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class RootPermissionsJson
{
    private final List<AccessControlEntryJson> accessControlList;

    public RootPermissionsJson( final AccessControlList contentPermissions, final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        final Principals principals = contentPrincipalsResolver.resolveAccessControlListPrincipals( contentPermissions );
        this.accessControlList = aclToJson( contentPermissions, principals );
    }

    private List<AccessControlEntryJson> aclToJson( final AccessControlList acl, final Principals principals )
    {
        final List<AccessControlEntryJson> jsonList = new ArrayList<>();
        for ( AccessControlEntry entry : acl )
        {
            jsonList.add( new AccessControlEntryJson( entry, principals.getPrincipal( entry.getPrincipal() ) ) );
        }
        return jsonList;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return this.accessControlList;
    }

}
