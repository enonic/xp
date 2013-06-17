package com.enonic.wem.api.command.resource;

import org.junit.Test;

public class GetResourceTest
{

    @Test
    public void testValidate()
        throws Exception
    {
        GetResource getResource = new GetResource();

        getResource.validate();
    }
}
