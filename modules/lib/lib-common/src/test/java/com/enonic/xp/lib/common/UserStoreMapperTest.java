package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.testing.helper.JsonAssert;

public class UserStoreMapperTest
{
    @Test
    public void testUserStoreSerialized()
        throws Exception
    {
        final UserStoreMapper userstoreMapper = new UserStoreMapper( TestDataFixtures.getTestUserStore(), true );
        JsonAssert.assertJson( getClass(), "userstore", userstoreMapper );
    }
}
