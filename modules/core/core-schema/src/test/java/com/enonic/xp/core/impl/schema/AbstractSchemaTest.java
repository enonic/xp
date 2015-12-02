package com.enonic.xp.core.impl.schema;

import java.util.List;

import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.resource.MockResourceService;
import com.enonic.xp.resource.ResourceService;

public abstract class AbstractSchemaTest
{
    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    @Before
    public final void setup()
        throws Exception
    {
        this.resourceService = new MockResourceService( getClass().getClassLoader(), "bundles" );

        this.applicationService = Mockito.mock( ApplicationService.class );
        initialize();
    }

    protected abstract void initialize()
        throws Exception;

    private Application createApplication( final String key )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( applicationKey );
        Mockito.when( this.applicationService.getApplication( applicationKey ) ).thenReturn( application );

        return application;
    }

    protected final void addApplications( final String... keys )
    {
        final List<Application> list = Lists.newArrayList();

        for ( final String key : keys )
        {
            list.add( createApplication( key ) );

        }

        final Applications apps = Applications.from( list );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( apps );
    }
}
