package com.enonic.xp.app.system;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The admin events hub serves this resource by key, from a module that does not contain it.
 */
class AdminEventClientResourceTest
{
    private static final String RESOURCE = "/admin/event/client.js";

    @Test
    void theHubClientIsShipped()
        throws IOException
    {
        try (InputStream in = AdminEventClientResourceTest.class.getResourceAsStream( RESOURCE ))
        {
            assertNotNull( in, RESOURCE );

            final String script = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
            assertTrue( script.contains( "export function connect" ) );
            assertTrue( script.contains( "export class AdminEventsSocket" ) );
        }
    }
}
