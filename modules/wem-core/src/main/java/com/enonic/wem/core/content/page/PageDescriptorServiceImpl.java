package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;

public class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    public PageDescriptor getByKey( final PageDescriptorKey key )
    {
        return new GetPageDescriptorCommand().key( key ).execute();
    }
}
