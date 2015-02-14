package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.content.page.region.LayoutDescriptorNotFoundException;
import com.enonic.xp.resource.ResourceNotFoundException;

final class GetLayoutDescriptorCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorCommand>
{
    private DescriptorKey key;

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

    public GetLayoutDescriptorCommand key( final DescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
