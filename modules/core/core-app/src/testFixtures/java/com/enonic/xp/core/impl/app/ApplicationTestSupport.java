package com.enonic.xp.core.impl.app;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.resource.ResourceService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class ApplicationTestSupport
{
    private static final String ROOT_TEST_PATH = "src/test/resources";

    protected ResourceService resourceService;

    protected ApplicationService applicationService;

    private Map<ApplicationKey, MockApplication> apps;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.apps = new HashMap<>();

        this.applicationService = mock( ApplicationService.class );
        when( this.applicationService.getInstalledApplication( any() ) ).then(
            invocationOnMock -> apps.get( invocationOnMock.getArgument( 0 ) ) );
        when( this.applicationService.getInstalledApplications() ).then( invocationOnMock -> Applications.from( apps.values() ) );
        when( this.applicationService.list() ).then( invocationOnMock -> Applications.from( apps.values() ) );

        ApplicationFactoryService applicationFactoryService = mock( ApplicationFactoryService.class );
        when( applicationFactoryService.findActiveApplication( any() ) ).then(
            invocationOnMock -> Optional.ofNullable( apps.get( invocationOnMock.getArgument( 0 ) ) ) );
        when( applicationFactoryService.findActiveBundle( any() ) ).then(
            invocationOnMock -> Optional.ofNullable( apps.get( invocationOnMock.getArgument( 0 ) ) )
                .map( MockApplication::getBundle ) );
        when( applicationFactoryService.findResolver( any(), any() ) ).then( invocationOnMock -> {
            final Object argument = invocationOnMock.getArgument( 0 );
            return Optional.ofNullable( apps.get( argument ) ).map( MockApplication::getUrlResolver );
        } );

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        initialize();
    }

    protected abstract void initialize()
        throws Exception;

    protected final MockApplication addApplication( final String key, final String prefix )
    {
        return addApplication( key, Path.of( ROOT_TEST_PATH + prefix ) );
    }

    /**
     * An application served from the files of a directory through its classloader, as an application in development is.
     */
    protected final MockApplication addApplication( final String key, final Path resourcePath )
    {
        final MockApplication app = new MockApplication();
        app.setKey( ApplicationKey.from( key ) );
        app.setStarted( true );

        // the bundle names the application the resources are served for: without one they would be served as the system
        // application's, and what is cached of them never found current again
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getSymbolicName() ).thenReturn( key );
        when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        app.setBundle( bundle );
        app.setResourcePath( resourcePath );

        this.apps.put( app.getKey(), app );
        return app;
    }
}
