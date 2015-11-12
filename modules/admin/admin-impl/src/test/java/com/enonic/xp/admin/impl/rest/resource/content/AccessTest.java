package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.Test;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static com.enonic.xp.security.acl.Permission.READ_PERMISSIONS;
import static com.enonic.xp.security.acl.Permission.WRITE_PERMISSIONS;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class AccessTest
{

    @Test
    public void testFromPermissions()
        throws Exception
    {
        assertEquals( Access.READ, Access.fromPermissions( asList( READ ) ) );
        assertEquals( Access.WRITE, Access.fromPermissions( asList( READ, CREATE, MODIFY, DELETE ) ) );
        assertEquals( Access.PUBLISH, Access.fromPermissions( asList( READ, MODIFY, DELETE, CREATE, PUBLISH ) ) );
        assertEquals( Access.FULL,
                      Access.fromPermissions( asList( READ, MODIFY, DELETE, CREATE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ) ) );
        assertEquals( Access.CUSTOM, Access.fromPermissions( asList( READ, CREATE ) ) );
        assertEquals( Access.CUSTOM, Access.fromPermissions( asList( READ_PERMISSIONS, WRITE_PERMISSIONS ) ) );
    }
}