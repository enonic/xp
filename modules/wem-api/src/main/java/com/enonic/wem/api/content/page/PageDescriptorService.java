package com.enonic.wem.api.content.page;

import com.enonic.wem.api.command.content.page.CreatePageDescriptor;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final PageDescriptorKey key );

    PageDescriptor create( final CreatePageDescriptor command );
}
