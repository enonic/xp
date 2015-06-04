package com.enonic.xp.core.impl.content.page.part;

import org.junit.Before;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;

public abstract class AbstractPartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new PartDescriptorServiceImpl();
        this.service.setModuleService( this.moduleService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "parts/" + key.getName() + "/part.xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<part-component><display-name>" + key.getName() + "</display-name></part-component>";
    }
}
