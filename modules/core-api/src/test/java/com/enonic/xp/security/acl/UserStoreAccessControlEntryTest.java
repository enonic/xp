package com.enonic.xp.security.acl;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class UserStoreAccessControlEntryTest
{

    @Test
    public void testEquals()
    {
        final  UserStoreAccessControlEntry usace =  UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.ADMINISTRATOR ).
            build();

        assertEquals( usace, usace );
        assertEquals( usace.hashCode(), usace.hashCode() );

        final  UserStoreAccessControlEntry usace2 =  UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.ADMINISTRATOR ).
            build();

        assertEquals( usace, usace2 );
        assertEquals( usace.hashCode(), usace2.hashCode() );

        final  UserStoreAccessControlEntry usace3 = UserStoreAccessControlEntry.create(usace).build();

        assertEquals( usace, usace3 );
        assertEquals( usace.hashCode(), usace3.hashCode() );

    }

    @Test
    public void testNotEquals() {

        final  UserStoreAccessControlEntry usace =  UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.ADMINISTRATOR ).
            build();

        final  UserStoreAccessControlEntry usace2 =  UserStoreAccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            access( UserStoreAccess.READ ).
            build();

        assertNotEquals( usace, usace2 );
        assertNotEquals( usace.hashCode(), usace2.hashCode() );

        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).build();

        assertNotEquals( usace, ace );
        assertNotEquals( usace.hashCode(), ace.hashCode() );

    }

}