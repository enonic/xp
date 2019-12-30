package com.enonic.xp.security.acl;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessControlEntryTest
{

    @Test
    public void testAccessControlEntry()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        assertEquals( PrincipalKey.ofAnonymous(), ace.getPrincipal() );
        assertEquals( "user:system:anonymous[+create, -modify, +delete, +read_permissions, -write_permissions]", ace.toString() );
        assertFalse( ace.isAllowed( Permission.MODIFY ) );
        assertTrue( ace.isDenied( Permission.MODIFY ) );
    }

    @Test
    public void testNoPermissions()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            build();

        assertEquals( PrincipalKey.ofAnonymous(), ace.getPrincipal() );
        assertEquals( "user:system:anonymous[]", ace.toString() );
        assertTrue( ace.isDenied( Permission.MODIFY ) );
        assertTrue( ace.isDenied( Permission.CREATE ) );
        assertIterableEquals( List.of(), ace.getAllowedPermissions() );
        assertIterableEquals( List.of(), ace.getDeniedPermissions() );
    }

    @Test
    public void testEquals()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        final AccessControlEntry ace2 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            deny( Permission.WRITE_PERMISSIONS ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            allow( Permission.DELETE ).
            allow( Permission.CREATE ).
            build();

        assertEquals( ace, ace2 );
        assertEquals( ace.hashCode(), ace2.hashCode() );
    }

    @Test
    public void testCopy()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        final AccessControlEntry newAce = AccessControlEntry.create( ace ).
            principal( PrincipalKey.ofAnonymous() ).
            remove( Permission.WRITE_PERMISSIONS ).
            remove( Permission.READ_PERMISSIONS ).
            deny( Permission.DELETE ).
            deny( Permission.CREATE ).
            build();

        assertEquals( "user:system:anonymous[-create, -modify, -delete]", newAce.toString() );
    }

}
