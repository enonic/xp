package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.PartDescriptor;
import com.enonic.xp.content.page.region.PartDescriptorNotFoundException;
import com.enonic.xp.resource.ResourceNotFoundException;

final class GetPartDescriptorCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorCommand>
{
    private DescriptorKey key;

    public PartDescriptor execute()
    {
        try
        {
            return getDescriptor( this.key );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PartDescriptorNotFoundException( this.key, e );
        }
    }

    public GetPartDescriptorCommand key( final DescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
