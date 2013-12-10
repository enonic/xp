package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;

public class GetPageDescriptor
    extends Command<PageDescriptor>
{
    private final PageDescriptorKey key;

    public GetPageDescriptor( final PageDescriptorKey key )
    {
        this.key = key;
    }

    public PageDescriptorKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }
}
