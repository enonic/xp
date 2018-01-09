package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.testing.helper.JsonAssert;

public class AuthDescriptorMapperTest
{
    @Test
    public void testUserStoreSerialized()
        throws Exception
    {
        final AuthDescriptorMapper authDescriptorMapper = new AuthDescriptorMapper( TestDataFixtures.getTestAuthDescriptor() );
        JsonAssert.assertJson( getClass(), "authdescriptor", authDescriptorMapper );
    }
}
