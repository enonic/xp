package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;

public class LayoutDescriptorServiceImpl_getByKeyTest
    extends AbstractLayoutDescriptorServiceTest
{
    @Test
    public void getPageDescriptor()
        throws Exception
    {
        final DescriptorKey key = createDescriptor( "foomodule:layout-descr" );
        final LayoutDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }
}
