package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class NodePermissionsResolver
{
    public static void requireContextUserPermissionOrAdmin( final Permission permission, final Node node )
        throws NodeAccessException
    {
        if ( node == null )
        {
            throw new IllegalArgumentException( "Node cannot be null" );
        }

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasPermission = hasPermission( authInfo.getPrincipals(), permission, node.getPermissions() );
        if ( !hasPermission )
        {
            throw new NodeAccessException( authInfo.getUser(), node.path(), permission );
        }
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                             final AccessControlList nodePermissions )
    {
        return hasPermission( authInfo.getPrincipals(), permission, nodePermissions );
    }

    public static boolean hasPermission( final PrincipalKeys principals, final Permission permission,
                                         final AccessControlList nodePermissions )
    {
        if ( principals.contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return nodePermissions.isAllowedFor( principals, permission );
    }
}
