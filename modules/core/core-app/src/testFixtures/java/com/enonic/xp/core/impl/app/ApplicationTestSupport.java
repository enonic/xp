package com.enonic.xp.core.impl.app;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.resource.ResourceService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class ApplicationTestSupport
{
    protected ResourceService resourceService;

    protected ApplicationService applicationService;

    private Map<ApplicationKey, MockApplication> apps;

    private final URL rootTestUrl;

    public ApplicationTestSupport()
    {
        try
        {
            this.rootTestUrl = Path.of( "src/test/resources" ).toUri().toURL();
        }
        catch ( MalformedURLException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.apps = new HashMap<>();

        this.applicationService = mock( ApplicationService.class );
        when( this.applicationService.getInstalledApplication( any() ) ).
            then( invocationOnMock -> apps.get( invocationOnMock.getArgument( 0 ) ) );
        when( this.applicationService.getInstalledApplications() ).
            then( invocationOnMock -> Applications.from( apps.values() ) );
        when( this.applicationService.getInstalledApplicationKeys() ).
            then( invocationOnMock -> ApplicationKeys.from( apps.keySet() ) );

        ApplicationFactoryService applicationFactoryService = mock( ApplicationFactoryService.class );
        when( applicationFactoryService.findActiveApplication( any() ) ).
            then( invocationOnMock -> Optional.ofNullable( apps.get( invocationOnMock.getArgument( 0 ) ) ) );

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        initialize();
    }

    protected abstract void initialize()
        throws Exception;

    protected final MockApplication addApplication( final String key, final String prefix )
    {
        final MockApplication app = new MockApplication();
        app.setKey( ApplicationKey.from( key ) );
        app.setStarted( true );

        app.setUrlResolver( this.rootTestUrl, prefix );

        this.apps.put( app.getKey(), app );
        return app;
    }
}
