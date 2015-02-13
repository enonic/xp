package com.enonic.wem.core.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.LayoutDescriptor;

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
