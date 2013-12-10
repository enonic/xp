package com.enonic.wem.api.command.content.page.layout;


import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;

public final class LayoutDescriptorCommands
{
    public GetLayoutDescriptor getByKey( final LayoutDescriptorKey key )
    {
        return new GetLayoutDescriptor( key );
    }

    public CreateLayoutDescriptor create()
    {
        return new CreateLayoutDescriptor();
    }

    public CreateLayoutDescriptor create( final LayoutDescriptor layoutDescriptor )
    {
        return new CreateLayoutDescriptor( layoutDescriptor );
    }
}
