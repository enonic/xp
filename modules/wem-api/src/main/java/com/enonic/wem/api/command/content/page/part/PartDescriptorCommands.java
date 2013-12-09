package com.enonic.wem.api.command.content.page.part;


import com.enonic.wem.api.content.page.part.PartDescriptor;

public final class PartDescriptorCommands
{
    public CreatePartDescriptor create()
    {
        return new CreatePartDescriptor();
    }

    public CreatePartDescriptor create( final PartDescriptor partDescriptor )
    {
        return new CreatePartDescriptor( partDescriptor );
    }
}
