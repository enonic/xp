package com.enonic.xp.repo.impl.entity;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static org.junit.Assert.*;

public class NodePermissionsResolverTest
{

    private static final UserStoreKey USER_STORE_KEY = UserStoreKey.from( "us" );

    private static final PrincipalKey USER_A = PrincipalKey.ofGroup( USER_STORE_KEY, "userA" );

    private static final PrincipalKey GROUP_B = PrincipalKey.ofGroup( USER_STORE_KEY, "groupB" );

    private static final PrincipalKey ROLE_C = PrincipalKey.ofRole( "roleC" );

    @Test
    public void hasPermissionEmptyACL()
        throws Exception
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
    public void hasPermissionAll()
        throws Exception
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
    public void hasPermissionSome()
        throws Exception
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
    public void system_admin_has_permission()
        throws Exception
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