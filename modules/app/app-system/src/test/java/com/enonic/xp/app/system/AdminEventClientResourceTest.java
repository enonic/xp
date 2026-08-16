package com.enonic.xp.app.system;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.MockApplication;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The admin events hub serves this resource by key, from a module that does not contain it, so
 * neither side alone can tell that the key still resolves to the script.
 */
class AdminEventClientResourceTest
{
    private static final String PATH = "/admin/event/client.js";

    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    @Test
    void theHubClientResolvesUnderTheSystemKey()
        throws URISyntaxException
    {
        final ResourceService resourceService = resourceServiceOverTheseResources();

        // the key AdminEventClient asks for
        final Resource resource = resourceService.getResource( ResourceKey.from( ApplicationKey.SYSTEM, PATH ) );

        assertTrue( resource.exists(), PATH );
        final String script = resource.readString();
        assertTrue( script.contains( "export function connect" ) );
        assertTrue( script.contains( "export class AdminEventsSocket" ) );
    }

    private ResourceService resourceServiceOverTheseResources()
        throws URISyntaxException
    {
        final MockApplication application = new MockApplication();
        application.setStarted( true );
        application.setResourcePath( resourcesRoot() );

        final ApplicationFactoryService applicationFactoryService = mock( ApplicationFactoryService.class );
        when( applicationFactoryService.findResolver( any(), any() ) ).thenReturn( Optional.empty() );
        when( applicationFactoryService.findResolver( SYSTEM_APPLICATION_KEY, null ) ).thenReturn(
            Optional.of( application.getUrlResolver() ) );

        return new ResourceServiceImpl( applicationFactoryService );
    }

    /**
     * Where the resources of this module are, whether the tests run against the jar or against
     * the exploded output.
     */
    private static Path resourcesRoot()
        throws URISyntaxException
    {
        final URL url = AdminEventClientResourceTest.class.getResource( PATH );
        assertNotNull( url, PATH );

        if ( "jar".equals( url.getProtocol() ) )
        {
            // jar:file:/../app-system.jar!/admin/event/client.js -> the jar
            final String path = url.getPath();
            return Path.of( URI.create( path.substring( 0, path.indexOf( '!' ) ) ) );
        }

        // ../resources/main/admin/event/client.js -> ../resources/main
        return Path.of( url.toURI() ).getParent().getParent().getParent();
    }
}
