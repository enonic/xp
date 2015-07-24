package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.resource.ResourceServiceImpl;
import com.enonic.xp.region.LayoutDescriptors;

public class LayoutDescriptorServiceImpl_getByApplicationsTest
    extends AbstractLayoutDescriptorServiceTest
{

    @Before
    public final void setupMe()
    {
        this.resourceService = Mockito.mock( ResourceServiceImpl.class );
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Application application = createApplication( "foomodule" );
        createDescriptors( "foomodule:foomodule-layout-descr" );

        mockResources( application, "/site/layouts", "*.xml", true, "site/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        final LayoutDescriptors result = this.service.getByModule( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Applications applications = createApplications( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-layout-descr", "barmodule:barmodule-layout-descr" );

        mockResources( applications.getModule( ApplicationKey.from( "foomodule" ) ), "/site/layouts", "*.xml", true,
                       "site/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        mockResources( applications.getModule( ApplicationKey.from( "barmodule" ) ), "/site/layouts", "*.xml", true,
                       "site/layouts/barmodule-layout-descr/barmodule-layout-descr.xml" );

        final LayoutDescriptors result = this.service.getByModules( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
