package com.enonic.xp.core.impl.content.page.part;

import org.junit.Before;

import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractPartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new PartDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
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
