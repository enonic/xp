package com.enonic.xp.security.acl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessControlListTest
{

    @Test
    void testCopy()
    {
        final AccessControlEntry entry1 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.READ ).
            build();
        final AccessControlEntry entry2 = AccessControlEntry.create().
            principal( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            allow( Permission.MODIFY ).
            build();

        final AccessControlList emptyAcl = AccessControlList.empty();
        final AccessControlList copyAcl = AccessControlList.create( emptyAcl ).add( entry1 ).build();
        final AccessControlList copyAcl2 = AccessControlList.create( copyAcl ).add( entry2 ).build();
        final AccessControlList copyAcl3 = AccessControlList.create( copyAcl2 ).remove( PrincipalKey.ofAnonymous() ).build();

        assertEquals( "[]", emptyAcl.toString() );
        assertEquals( "[user:system:anonymous[+read]]", copyAcl.toString() );
        assertEquals( "[user:system:anonymous[+read], user:system:user1[+modify]]", copyAcl2.toString() );
        assertEquals( "[user:system:user1[+modify]]", copyAcl3.toString() );
    }

}
