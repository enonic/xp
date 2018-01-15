package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.security.acl.UserStoreAccess;
import com.enonic.xp.testing.helper.JsonAssert;

public class PermissionMapperTest
{
    @Test
    public void testSerialized()
        throws Exception
    {
        final PermissionMapper permissionMapper = new PermissionMapper( TestDataFixtures.getTestUser(), UserStoreAccess.READ );
        JsonAssert.assertJson( getClass(), "read", permissionMapper );
    }
}
