package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.content.page.PageDescriptor;

public final class PageDescriptorCommands
{
    public CreatePageDescriptor create()
    {
        return new CreatePageDescriptor();
    }

    public CreatePageDescriptor create( final PageDescriptor pageDescriptor )
    {
        return new CreatePageDescriptor( pageDescriptor );
    }
}
