package com.enonic.xp.core.impl.content.page;

import java.net.URL;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PageDescriptorServiceImpl service;

    @Before
    public final void setupService()
        throws Exception
    {
        this.service = new PageDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        Resource res = new Resource( ResourceKey.from( "foomodule:/site/pages/page-descr/page-descr.xml" ),
                                     new URL( "module:foomodule:/site/pages/page-descr/page-descr.xml" ) );
        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).thenReturn( res );

    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<page><display-name>" + key.getName() + "</display-name></page>";
    }
}
