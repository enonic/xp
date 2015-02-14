package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Before;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.resource.ResourceKey;
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
        return ResourceKey.from( key.getModuleKey(), "cms/layouts/" + key.getName() + "/layout.xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<layout-component><display-name>" + key.getName() + "</display-name></layout-component>";
    }
}
