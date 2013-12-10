package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;

public final class PageDescriptorCommands
{
    public GetPageDescriptor getByKey( final PageDescriptorKey key )
    {
        return new GetPageDescriptor( key );
    }

    public CreatePageDescriptor create()
    {
        return new CreatePageDescriptor();
    }

    public CreatePageDescriptor create( final PageDescriptor pageDescriptor )
    {
        return new CreatePageDescriptor( pageDescriptor );
    }
}
