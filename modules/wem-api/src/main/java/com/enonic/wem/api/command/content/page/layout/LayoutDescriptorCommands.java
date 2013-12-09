package com.enonic.wem.api.command.content.page.layout;


import com.enonic.wem.api.content.page.layout.LayoutDescriptor;

public final class LayoutDescriptorCommands
{
    public CreateLayoutDescriptor create()
    {
        return new CreateLayoutDescriptor();
    }

    public CreateLayoutDescriptor create( final LayoutDescriptor layoutDescriptor )
    {
        return new CreateLayoutDescriptor( layoutDescriptor );
    }
}
