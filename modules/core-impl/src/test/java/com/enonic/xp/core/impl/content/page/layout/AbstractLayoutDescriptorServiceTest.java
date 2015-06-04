package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Before;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.LayoutDescriptorServiceImpl;

public abstract class AbstractLayoutDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected LayoutDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new LayoutDescriptorServiceImpl();
        this.service.setModuleService( this.moduleService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "layouts/" + key.getName() + "/layout.xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<layout-component><display-name>" + key.getName() + "</display-name></layout-component>";
    }
}
