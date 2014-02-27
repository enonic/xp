package com.enonic.wem.api.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;

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
