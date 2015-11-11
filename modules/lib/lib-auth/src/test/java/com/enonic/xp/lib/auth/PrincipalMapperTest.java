package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.testing.json.JsonAssert;

public class PrincipalMapperTest
{
    @Test
    public void testUserSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestUser() );
        JsonAssert.assertJson( getClass(), "user", principalMapper );
    }

    @Test
    public void testGroupSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestGroup() );
        JsonAssert.assertJson( getClass(), "group", principalMapper );
    }

    @Test
    public void testRoleSerialized()
        throws Exception
    {
        final PrincipalMapper principalMapper = new PrincipalMapper( TestDataFixtures.getTestRole() );
        JsonAssert.assertJson( getClass(), "role", principalMapper );
    }
}
