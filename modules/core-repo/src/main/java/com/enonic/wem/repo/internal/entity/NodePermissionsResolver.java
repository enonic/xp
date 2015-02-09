package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

final class NodePermissionsResolver
{

    public boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
    {
        return userHasPermission( authInfo, permission, node.getPermissions() );
    }

    public boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                      final AccessControlList nodePermissions )
    {
        final PrincipalKeys authInfoPrincipals = authInfo.getPrincipals();
        final PrincipalKeys principalsAllowed = nodePermissions.getPrincipalsWithPermission( permission );

        return principalsAllowed.stream().anyMatch( ( authInfoPrincipals::contains ) );
    }

}
