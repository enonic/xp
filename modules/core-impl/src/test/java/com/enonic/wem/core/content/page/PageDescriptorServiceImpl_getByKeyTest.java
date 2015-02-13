package com.enonic.wem.core.content.page;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;

public class PageDescriptorServiceImpl_getByKeyTest
    extends AbstractPageDescriptorServiceTest
{
    @Test
    public void getPageDescriptor()
        throws Exception
    {
        final DescriptorKey key = createDescriptor( "foomodule:page-descr" );
        final PageDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }
}
