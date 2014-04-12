package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.resource.ResourceService;

public class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    @Inject
    protected ResourceService resourceService;

    public PageDescriptor getByKey( final PageDescriptorKey key )
    {
        return new GetPageDescriptorCommand().key( key ).resourceService( this.resourceService ).execute();
    }
}
