package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;

public class GetLayoutDescriptor
    extends Command<LayoutDescriptor>
{
    private final LayoutDescriptorKey key;

    public GetLayoutDescriptor( final LayoutDescriptorKey key )
    {
        this.key = key;
    }

    public LayoutDescriptorKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }
}
