package com.enonic.xp.security.acl;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;

public class UserStoreAccessControlListTest
{

    @Test
    public void testCopy()
    {
        final UserStoreAccessControlEntry entry1 = UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.READ ).
            build();
        final UserStoreAccessControlEntry entry2 =UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            access( UserStoreAccess.CREATE_USERS ).
            build();

        final UserStoreAccessControlList emptyAcl = UserStoreAccessControlList.empty();
        final UserStoreAccessControlList copyAcl = UserStoreAccessControlList.create( emptyAcl ).add( entry1 ).build();
        final UserStoreAccessControlList copyAcl2 = UserStoreAccessControlList.create( copyAcl ).add( entry2 ).build();
        final UserStoreAccessControlList copyAcl3 = UserStoreAccessControlList.create( copyAcl2 ).remove( PrincipalKey.ofAnonymous() ).build();

        assertEquals( "[]", emptyAcl.toString() );
        assertEquals( "[user:system:anonymous[read]]", copyAcl.toString() );
        assertEquals( "[user:system:anonymous[read], user:system:user1[create_users]]", copyAcl2.toString() );
        assertEquals( "[user:system:user1[create_users]]", copyAcl3.toString() );
    }

    @Test
    public void testEquals()
    {

        final UserStoreAccessControlEntry entry1 = UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.READ ).
            build();
        final UserStoreAccessControlEntry entry2 =UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            access( UserStoreAccess.CREATE_USERS ).
            build();

        final UserStoreAccessControlList acl = UserStoreAccessControlList.create( ).add( entry1 ).add( entry2 ).build();
        final UserStoreAccessControlList acl2 = UserStoreAccessControlList.create().addAll( entry1, entry2 ).build();
        final UserStoreAccessControlList acl3 = UserStoreAccessControlList.create().addAll( Arrays.asList( entry1, entry2 ) ).build();

        assertEquals( acl, acl2 );
        assertEquals( acl2, acl3 );
        assertEquals( acl, acl3 );

        assertEquals( acl.hashCode(), acl2.hashCode());
        assertEquals( acl2.hashCode(), acl3.hashCode());
        assertEquals( acl.hashCode(), acl3.hashCode());

    }

    @Test
    public void testNotEquals() {

        final UserStoreAccessControlEntry entry1 = UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.READ ).
            build();
        final UserStoreAccessControlEntry entry2 =UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            access( UserStoreAccess.CREATE_USERS ).
            build();

        final UserStoreAccessControlList emptyAcl = UserStoreAccessControlList.empty();
        final UserStoreAccessControlList acl = UserStoreAccessControlList.create( ).add( entry1 ).add( entry2 ).build();
        final UserStoreAccessControlList acl2 = UserStoreAccessControlList.create().addAll( entry1, entry2 ).build();

        assertNotEquals( emptyAcl, acl );
        assertNotEquals( emptyAcl, acl2 );

    }

}