package com.enonic.wem.api.command.content.page.part;


import com.enonic.wem.api.content.page.part.PartDescriptorKey;

public final class PartDescriptorCommands
{
    public GetPartDescriptor getByKey( final PartDescriptorKey key )
    {
        return new GetPartDescriptor( key );
    }

    public CreatePartDescriptor create()
    {
        return new CreatePartDescriptor();
    }
}
