package com.enonic.xp.core.impl.app;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;

public abstract class ApplicationTestSupport
{
    protected ResourceServiceImpl resourceService;

    protected ApplicationService applicationService;

    private Map<ApplicationKey, Application> apps;

    private URL rootTestUrl;

    @Before
    public final void setup()
        throws Exception
    {
        this.rootTestUrl = new File( "./src/test/resources" ).toURI().toURL();

        this.apps = Maps.newHashMap();

        this.applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( this.applicationService.getApplication( Mockito.any() ) ).then(
            invocationOnMock -> apps.get( (ApplicationKey) invocationOnMock.getArguments()[0] ) );
        Mockito.when( this.applicationService.getAllApplications() ).then( invocationOnMock -> Applications.from( apps.values() ) );
        Mockito.when( this.applicationService.getApplicationKeys() ).then( invocationOnMock -> ApplicationKeys.from( apps.keySet() ) );

        this.resourceService = new ResourceServiceImpl();
        this.resourceService.setApplicationService( this.applicationService );

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
