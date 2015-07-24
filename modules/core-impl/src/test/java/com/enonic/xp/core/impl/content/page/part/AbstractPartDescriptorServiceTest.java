package com.enonic.xp.core.impl.content.page.part;

import java.net.URL;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractPartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Before
    public final void setupService()
        throws Exception
    {
        this.service = new PartDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        Resource res = new Resource( ResourceKey.from( "foomodule:/site/parts/part-descr/part-descr.xml" ),
                                     new URL( "module:foomodule:/site/parts/part-descr/part-descr.xml" ) );
        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).thenReturn( res );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<part><display-name>" + key.getName() + "</display-name></part>";
    }
}
