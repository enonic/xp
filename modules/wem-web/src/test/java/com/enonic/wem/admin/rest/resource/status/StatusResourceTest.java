package com.enonic.wem.admin.rest.resource.status;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusResourceTest
{
    @Test
    public void testGetStatus()
    {
        final StatusResource resource = new StatusResource();
        final StatusResult result = resource.getStatus();
        assertNotNull( result );
    }
}
