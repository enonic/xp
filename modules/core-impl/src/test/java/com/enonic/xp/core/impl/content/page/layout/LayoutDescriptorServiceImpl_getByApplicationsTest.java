package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.LayoutDescriptors;

public class LayoutDescriptorServiceImpl_getByModulesTest
    extends AbstractLayoutDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Application application = createModule( "foomodule" );
        createDescriptors( "foomodule:foomodule-layout-descr" );

        mockResources( application, "/app/layouts", "*.xml", true, "app/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        final LayoutDescriptors result = this.service.getByModule( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Modules modules = createModules( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-layout-descr", "barmodule:barmodule-layout-descr" );

        mockResources( modules.getModule( ApplicationKey.from( "foomodule" ) ), "/app/layouts", "*.xml", true,
                       "app/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        mockResources( modules.getModule( ApplicationKey.from( "barmodule" ) ), "/app/layouts", "*.xml", true,
                       "app/layouts/barmodule-layout-descr/barmodule-layout-descr.xml" );

        final LayoutDescriptors result = this.service.getByModules( modules.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
