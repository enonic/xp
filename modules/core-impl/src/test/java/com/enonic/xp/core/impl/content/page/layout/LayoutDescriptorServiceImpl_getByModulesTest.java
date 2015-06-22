package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.Modules;
import com.enonic.xp.region.LayoutDescriptors;

public class LayoutDescriptorServiceImpl_getByModulesTest
    extends AbstractLayoutDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        createDescriptors( "foomodule:foomodule-layout-descr" );

        mockResourcePaths( module, "app/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        final LayoutDescriptors result = this.service.getByModule( module.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Modules modules = createModules( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-layout-descr", "barmodule:barmodule-layout-descr" );

        mockResourcePaths( modules.getModule( ModuleKey.from( "foomodule" ) ),
                           "app/layouts/foomodule-layout-descr/foomodule-layout-descr.xml" );
        mockResourcePaths( modules.getModule( ModuleKey.from( "barmodule" ) ),
                           "app/layouts/barmodule-layout-descr/barmodule-layout-descr.xml" );

        final LayoutDescriptors result = this.service.getByModules( modules.getModuleKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
