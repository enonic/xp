package com.enonic.xp.core.impl.content.page.layout;

import java.net.URL;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.LayoutDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractLayoutDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected LayoutDescriptorServiceImpl service;

    @Before
    public final void setupService()
        throws Exception
    {
        this.service = new LayoutDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        Resource res = new Resource( ResourceKey.from( "foomodule:/site/layouts/layout-descr/layout-descr.xml" ),
                                     new URL( "module:foomodule:/site/layouts/layout-descr/layout-descr.xml" ) );
        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).thenReturn( res );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/layouts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<layout><display-name>" + key.getName() + "</display-name></layout>";
    }
}
