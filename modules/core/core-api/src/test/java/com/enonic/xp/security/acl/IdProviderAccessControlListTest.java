package com.enonic.xp.security.acl;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IdProviderAccessControlListTest
{

    @Test
    void testCopy()
    {
        final IdProviderAccessControlEntry entry1 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.READ ).
            build();
        final IdProviderAccessControlEntry entry2 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            access( IdProviderAccess.CREATE_USERS ).
            build();

        final IdProviderAccessControlList emptyAcl = IdProviderAccessControlList.empty();
        final IdProviderAccessControlList copyAcl = IdProviderAccessControlList.create( emptyAcl ).add( entry1 ).build();
        final IdProviderAccessControlList copyAcl2 = IdProviderAccessControlList.create( copyAcl ).add( entry2 ).build();
        final IdProviderAccessControlList copyAcl3 =
            IdProviderAccessControlList.create( copyAcl2 ).remove( PrincipalKey.ofAnonymous() ).build();

        assertEquals( "[]", emptyAcl.toString() );
        assertEquals( "[user:system:anonymous[read]]", copyAcl.toString() );
        assertEquals( "[user:system:anonymous[read], user:system:user1[create_users]]", copyAcl2.toString() );
        assertEquals( "[user:system:user1[create_users]]", copyAcl3.toString() );
    }

    @Test
    void testEquals()
    {

        final IdProviderAccessControlEntry entry1 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.READ ).
            build();
        final IdProviderAccessControlEntry entry2 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            access( IdProviderAccess.CREATE_USERS ).
            build();

        final IdProviderAccessControlList acl = IdProviderAccessControlList.create().add( entry1 ).add( entry2 ).build();
        final IdProviderAccessControlList acl2 = IdProviderAccessControlList.create().addAll( entry1, entry2 ).build();
        final IdProviderAccessControlList acl3 = IdProviderAccessControlList.create().addAll( Arrays.asList( entry1, entry2 ) ).build();

        assertEquals( acl, acl2 );
        assertEquals( acl2, acl3 );
        assertEquals( acl, acl3 );

        assertEquals( acl.hashCode(), acl2.hashCode());
        assertEquals( acl2.hashCode(), acl3.hashCode());
        assertEquals( acl.hashCode(), acl3.hashCode());

    }

    @Test
    void testNotEquals() {

        final IdProviderAccessControlEntry entry1 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.READ ).
            build();
        final IdProviderAccessControlEntry entry2 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            access( IdProviderAccess.CREATE_USERS ).
            build();

        final IdProviderAccessControlList emptyAcl = IdProviderAccessControlList.empty();
        final IdProviderAccessControlList acl = IdProviderAccessControlList.create().add( entry1 ).add( entry2 ).build();
        final IdProviderAccessControlList acl2 = IdProviderAccessControlList.create().addAll( entry1, entry2 ).build();

        assertNotEquals( emptyAcl, acl );
        assertNotEquals( emptyAcl, acl2 );

    }

}
