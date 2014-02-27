package com.enonic.wem.api.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;

public class GetPartDescriptor
    extends Command<PartDescriptor>
{
    private final PartDescriptorKey key;

    public GetPartDescriptor( final PartDescriptorKey key )
    {
        this.key = key;
    }

    public PartDescriptorKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }
}
