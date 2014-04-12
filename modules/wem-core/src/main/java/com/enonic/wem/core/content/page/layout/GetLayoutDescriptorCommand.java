package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;

final class GetLayoutDescriptorCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorCommand>
{
    private LayoutDescriptorKey key;

    public LayoutDescriptor execute()
    {
        try
        {
            return getDescriptor( this.key );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new LayoutDescriptorNotFoundException( this.key, e );
        }
    }

    public GetLayoutDescriptorCommand key( final LayoutDescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
