package com.enonic.xp.core.impl.content.page;

import org.junit.Before;

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
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "app/pages/" + key.getName() + "/page.xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<page><display-name>" + key.getName() + "</display-name></page>";
    }
}
