package com.enonic.wem.repo.internal.entity;

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
    public static void requireContextUserPermission( final Permission permission, final Node node )
        throws NodeAccessException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        doRequireContextUserPermission( authInfo, permission, node );
    }

    public static void requireContextUserPermissionOrAdmin( final Permission permission, final Node node )
        throws NodeAccessException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return;
        }
        doRequireContextUserPermission( authInfo, permission, node );
    }

    public static void requireContextUserPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
        throws NodeAccessException
    {
        doRequireContextUserPermission( authInfo, permission, node );
    }

    private static void doRequireContextUserPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
    {
        final boolean hasPermission = doUserHasPermission( authInfo, permission, node.getPermissions() );
        if ( !hasPermission )
        {
            throw new NodeAccessException( authInfo.getUser(), node.path(), permission );
        }
    }

    public static boolean contextUserHasPermission( final Permission permission, final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return userHasPermission( authInfo, permission, node );
    }

    public static boolean contextUserHasPermissionOrAdmin( final Permission permission, final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.getPrincipals().contains( RoleKeys.ADMIN ) || userHasPermission( authInfo, permission, node );
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
    {
        return doUserHasPermission( authInfo, permission, node.getPermissions() );
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                             final AccessControlList nodePermissions )
    {
        return doUserHasPermission( authInfo, permission, nodePermissions );
    }

    private static boolean doUserHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                                final AccessControlList nodePermissions )
    {
        if ( authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            return true;
        }

        final PrincipalKeys authInfoPrincipals = authInfo.getPrincipals();
        final PrincipalKeys principalsAllowed = nodePermissions.getPrincipalsWithPermission( permission );

        return principalsAllowed.stream().anyMatch( ( authInfoPrincipals::contains ) );
    }

}
