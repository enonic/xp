package com.enonic.wem.api.security.acl;

import org.junit.Test;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;

import static org.junit.Assert.*;

public class AccessControlListTest
{

    @Test
    public void testEffectiveWithoutOverlap()
    {
        final PrincipalKey user1 = PrincipalKey.ofUser( UserStoreKey.system(), "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( UserStoreKey.system(), "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( UserStoreKey.system(), "group1" );
        final PrincipalKey group2 = PrincipalKey.ofGroup( UserStoreKey.system(), "group2" );
        final PrincipalKey group3 = PrincipalKey.ofGroup( UserStoreKey.system(), "group3" );

        final AccessControlEntry aceUser1 = AccessControlEntry.create().
            principal( user1 ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            build();

        final AccessControlEntry aceUser2 = AccessControlEntry.create().
            principal( user2 ).
            allow( Permission.READ ).
            build();

        final AccessControlEntry aceGroup1 = AccessControlEntry.create().
            principal( group1 ).
            allow( Permission.READ ).
            allow( Permission.MODIFY ).
            build();

        final AccessControlEntry aceGroup2 = AccessControlEntry.create().
            principal( group2 ).
            allow( Permission.READ ).
            allow( Permission.MODIFY ).
            build();

        final AccessControlEntry aceGroup3 = AccessControlEntry.create().
            principal( group3 ).
            build();

        final AccessControlList parentAcl = AccessControlList.of( aceUser1, aceGroup2, aceGroup3 );
        final AccessControlList childAcl = AccessControlList.of( aceUser2, aceGroup1 );
        final AccessControlList effectiveAcl = childAcl.getEffective( parentAcl );

        assertEquals( "[group:system:group2[+read, +modify], group:system:group3[], user:system:user1[+create, +delete]]",
                      parentAcl.toString() );
        assertEquals( "[group:system:group1[+read, +modify], user:system:user2[+read]]", childAcl.toString() );

        assertEquals( "[group:system:group2[+read, -create, +modify, -delete, -publish, -read_permissions, -write_permissions], " +
                          "group:system:group1[+read, -create, +modify, -delete, -publish, -read_permissions, -write_permissions], " +
                          "group:system:group3[-read, -create, -modify, -delete, -publish, -read_permissions, -write_permissions], " +
                          "user:system:user1[-read, +create, -modify, +delete, -publish, -read_permissions, -write_permissions], " +
                          "user:system:user2[+read, -create, -modify, -delete, -publish, -read_permissions, -write_permissions]]",
                      effectiveAcl.toString() );
    }

    @Test
    public void testEffectiveWithInheritance()
    {
        final PrincipalKey user1 = PrincipalKey.ofUser( UserStoreKey.system(), "user1" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( UserStoreKey.system(), "group1" );

        final AccessControlEntry aceParentGroup1 = AccessControlEntry.create().
            principal( group1 ).
            allow( Permission.READ ).
            allow( Permission.MODIFY ).
            allow( Permission.PUBLISH ).
            deny( Permission.DELETE ).
            build();

        final AccessControlEntry aceUser1 = AccessControlEntry.create().
            principal( user1 ).
            allow( Permission.READ ).
            build();

        final AccessControlEntry aceGroup1 = AccessControlEntry.create().
            principal( group1 ).
            deny( Permission.READ ).
            allow( Permission.MODIFY ).
            allow( Permission.CREATE ).
            build();

        final AccessControlList parentAcl = AccessControlList.of( aceParentGroup1 );
        final AccessControlList childAcl = AccessControlList.of( aceUser1, aceGroup1 );
        final AccessControlList effectiveAcl = childAcl.getEffective( parentAcl );

        assertEquals( "[group:system:group1[+read, +modify, -delete, +publish]]", parentAcl.toString() );
        assertEquals( "[group:system:group1[-read, +create, +modify], user:system:user1[+read]]", childAcl.toString() );

        assertEquals( "[group:system:group1[-read, +create, +modify, -delete, +publish, -read_permissions, -write_permissions], " +
                          "user:system:user1[+read, -create, -modify, -delete, -publish, -read_permissions, -write_permissions]]",
                      effectiveAcl.toString() );

        assertTrue( effectiveAcl.isAllowedFor( Permission.READ, user1 ) );
        assertTrue( effectiveAcl.isDeniedFor( Permission.MODIFY, user1 ) );
        assertTrue( effectiveAcl.isDeniedFor( Permission.CREATE, user1 ) );
        assertTrue( effectiveAcl.isDeniedFor( Permission.DELETE, user1 ) );

        assertTrue( effectiveAcl.isDeniedFor( Permission.READ, group1 ) );
        assertTrue( effectiveAcl.isAllowedFor( Permission.MODIFY, group1 ) );
        assertTrue( effectiveAcl.isAllowedFor( Permission.CREATE, group1 ) );
        assertTrue( effectiveAcl.isDeniedFor( Permission.DELETE, group1 ) );
    }

    @Test
    public void testCopy()
    {
        final AccessControlEntry entry1 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.READ ).
            build();
        final AccessControlEntry entry2 = AccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            allow( Permission.MODIFY ).
            build();

        final AccessControlList emptyAcl = AccessControlList.empty();
        final AccessControlList copyAcl = AccessControlList.newACL( emptyAcl ).add( entry1 ).build();
        final AccessControlList copyAcl2 = AccessControlList.newACL( copyAcl ).add( entry2 ).build();
        final AccessControlList copyAcl3 = AccessControlList.newACL( copyAcl2 ).remove( PrincipalKey.ofAnonymous() ).build();

        assertEquals( "[]", emptyAcl.toString() );
        assertEquals( "[user:system:anonymous[+read]]", copyAcl.toString() );
        assertEquals( "[user:system:anonymous[+read], user:system:user1[+modify]]", copyAcl2.toString() );
        assertEquals( "[user:system:user1[+modify]]", copyAcl3.toString() );
    }

}