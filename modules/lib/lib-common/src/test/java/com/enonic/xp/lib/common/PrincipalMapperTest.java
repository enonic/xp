package com.enonic.xp.lib.common;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.helper.JsonAssert;

class PrincipalMapperTest
{
    @Test
    void testUserSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestUser() );
        JsonAssert.assertJson( getClass(), "user", principalMapper );
    }

    @Test
    void testGroupSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestGroup() );
        JsonAssert.assertJson( getClass(), "group", principalMapper );
    }

    @Test
    void testRoleSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestRole() );
        JsonAssert.assertJson( getClass(), "role", principalMapper );
    }
}
