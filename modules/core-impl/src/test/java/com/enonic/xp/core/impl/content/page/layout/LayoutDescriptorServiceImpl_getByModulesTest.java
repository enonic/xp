package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.content.page.region.LayoutDescriptors;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.Modules;

public class LayoutDescriptorServiceImpl_getByModulesTest
    extends AbstractLayoutDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        createDescriptors( "foomodule:foomodule-layout-descr" );

        mockResourcePaths( module, "layouts/foomodule-layout-descr/layout.xml" );
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

        mockResourcePaths( modules.getModule( ModuleKey.from( "foomodule" ) ), "layouts/foomodule-layout-descr/layout.xml" );
        mockResourcePaths( modules.getModule( ModuleKey.from( "barmodule" ) ), "layouts/barmodule-layout-descr/layout.xml" );

        final LayoutDescriptors result = this.service.getByModules( modules.getModuleKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
