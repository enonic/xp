package com.enonic.xp.admin.impl.widget;

import org.junit.Before;

import com.enonic.xp.page.AbstractDescriptorServiceTest;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public abstract class AbstractWidgetDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected WidgetDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new WidgetDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "/ui/widgets/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<widget>" +
            "<display-name>" + key.getName() + "</display-name>" +
            "<interfaces>" +
            "<interface>com.enonic.xp.my-interface</interface>" +
            "<interface>com.enonic.xp.my-interface-2</interface>" +
            "</interfaces>" +
            "</widget>";
    }
}
