package com.enonic.xp.core.impl.content.page.part;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.Modules;
import com.enonic.xp.region.PartDescriptors;

public class PartDescriptorServiceImpl_getByModulesTest
    extends AbstractPartDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        createDescriptors( "foomodule:foomodule-part-descr" );

        mockResources( module, "/app/parts", "*.xml", "app/parts/foomodule-part-descr/foomodule-part-descr.xml" );
        final PartDescriptors result = this.service.getByModule( module.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Modules modules = createModules( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-part-descr", "barmodule:barmodule-part-descr" );

        mockResources( modules.getModule( ApplicationKey.from( "foomodule" ) ), "/app/parts", "*.xml",
                       "app/parts/foomodule-part-descr/foomodule-part-descr.xml" );
        mockResources( modules.getModule( ApplicationKey.from( "barmodule" ) ), "/app/parts", "*.xml",
                       "app/parts/barmodule-part-descr/barmodule-part-descr.xml" );

        final PartDescriptors result = this.service.getByModules( modules.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
