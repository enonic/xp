package com.enonic.wem.core.content.page.part;

import org.junit.Before;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.core.content.page.AbstractDescriptorServiceTest;
import com.enonic.wem.core.content.page.region.PartDescriptorServiceImpl;

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
        return ResourceKey.from( key.getModuleKey(), "cms/parts/" + key.getName() + "/part.xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<part-component><display-name>" + key.getName() + "</display-name></part-component>";
    }
}
