package com.enonic.xp.security.acl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IdProviderAccessControlEntryTest
{

    @Test
    void testEquals()
    {
        final IdProviderAccessControlEntry usace = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.ADMINISTRATOR ).
            build();

        assertEquals( usace, usace );
        assertEquals( usace.hashCode(), usace.hashCode() );

        final IdProviderAccessControlEntry usace2 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.ADMINISTRATOR ).
            build();

        assertEquals( usace, usace2 );
        assertEquals( usace.hashCode(), usace2.hashCode() );

        final IdProviderAccessControlEntry usace3 = IdProviderAccessControlEntry.create( usace ).build();

        assertEquals( usace, usace3 );
        assertEquals( usace.hashCode(), usace3.hashCode() );

    }

    @Test
    void testNotEquals() {

        final IdProviderAccessControlEntry usace = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.ADMINISTRATOR ).
            build();

        final IdProviderAccessControlEntry usace2 = IdProviderAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( IdProviderAccess.READ ).
            build();

        assertNotEquals( usace, usace2 );
        assertNotEquals( usace.hashCode(), usace2.hashCode() );

        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).build();

        assertNotEquals( usace, ace );
        assertNotEquals( usace.hashCode(), ace.hashCode() );

    }

}
