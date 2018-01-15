package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.testing.helper.JsonAssert;

public class AuthConfigMapperTest
{
    @Test
    public void testUserStoreSerialized()
        throws Exception
    {
        final AuthConfigMapper authConfigMapper = new AuthConfigMapper( TestDataFixtures.getTestAuthConfig() );
        JsonAssert.assertJson( getClass(), "authconfig", authConfigMapper );
    }
}
