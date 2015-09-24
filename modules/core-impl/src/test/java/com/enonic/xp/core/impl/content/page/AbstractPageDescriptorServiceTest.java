package com.enonic.xp.core.impl.content.page;

import org.junit.Before;

import com.enonic.xp.page.AbstractDescriptorServiceTest;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PageDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new PageDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
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
