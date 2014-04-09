package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.module.ModuleService;

public class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    @Inject
    protected ModuleService moduleService;

    public PageDescriptor getByKey( final PageDescriptorKey key )
    {
        return new GetPageDescriptorCommand().key( key ).moduleService( this.moduleService ).execute();
    }
}
