package com.enonic.xp.core.impl.content.page.region;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.PartDescriptor;
import com.enonic.wem.api.content.page.region.PartDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;

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
