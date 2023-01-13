package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class NodePermissionsResolver
{
    public static void requireContextUserPermissionOrAdmin( final Permission permission, final Node node )
        throws NodeAccessException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasPermission = userHasPermission( authInfo, permission, node.getPermissions() );
        if ( !hasPermission )
        {
            throw new NodeAccessException( authInfo.getUser(), node.path(), permission );
        }
    }

    public static boolean contextUserHasPermissionOrAdmin( final Permission permission, final AccessControlList nodePermissions )
    {
        return userHasPermission( ContextAccessor.current().getAuthInfo(), permission, nodePermissions );
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                             final AccessControlList nodePermissions )
    {
        if ( authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return nodePermissions.isAllowedFor( authInfo.getPrincipals(), permission );
    }
}
