package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodePermissionsResolverTest
{

    private static final IdProviderKey ID_PROVIDER_KEY = IdProviderKey.from( "us" );

    private static final PrincipalKey USER_A = PrincipalKey.ofUser( ID_PROVIDER_KEY, "userA" );

    private static final PrincipalKey GROUP_B = PrincipalKey.ofGroup( ID_PROVIDER_KEY, "groupB" );

    private static final PrincipalKey ROLE_C = PrincipalKey.ofRole( "roleC" );

    @Test
    void hasPermissionEmptyACL()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( User.ANONYMOUS ).
            principals( PrincipalKey.ofAnonymous() ).
            build();

        final AccessControlList nodePermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( USER_A ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( GROUP_B ).allow( CREATE ).build() ).
            add( AccessControlEntry.create().principal( ROLE_C ).allow( MODIFY ).build() ).
            build();

        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, READ, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, CREATE, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, MODIFY, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, PUBLISH, nodePermissions ) );
    }

    @Test
    void hasPermissionAll()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( User.create().key( USER_A ).login( "usera" ).build() ).
            principals( USER_A, GROUP_B, ROLE_C ).
            build();

        final AccessControlList nodePermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( USER_A ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( GROUP_B ).allow( CREATE ).build() ).
            add( AccessControlEntry.create().principal( ROLE_C ).allow( READ, MODIFY, CREATE ).build() ).
            build();

        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, READ, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, CREATE, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, MODIFY, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, PUBLISH, nodePermissions ) );
    }

    @Test
    void hasPermissionSome()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( User.create().key( USER_A ).login( "usera" ).build() ).
            principals( USER_A, ROLE_C ).
            build();

        final AccessControlList nodePermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( USER_A ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( GROUP_B ).allow( CREATE ).build() ).
            add( AccessControlEntry.create().principal( ROLE_C ).allow( MODIFY ).build() ).
            build();

        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, READ, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, CREATE, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, MODIFY, nodePermissions ) );
        assertFalse( NodePermissionsResolver.userHasPermission( authInfo, PUBLISH, nodePermissions ) );
    }

    @Test
    void system_admin_has_permission()
    {

        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( User.create().key( USER_A ).login( "usera" ).build() ).
            principals( RoleKeys.ADMIN ).
            build();

        final AccessControlList nodePermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( USER_A ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( GROUP_B ).allow( CREATE ).build() ).
            add( AccessControlEntry.create().principal( ROLE_C ).allow( MODIFY ).build() ).
            build();

        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, READ, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, CREATE, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, MODIFY, nodePermissions ) );
        assertTrue( NodePermissionsResolver.userHasPermission( authInfo, PUBLISH, nodePermissions ) );

    }
}
