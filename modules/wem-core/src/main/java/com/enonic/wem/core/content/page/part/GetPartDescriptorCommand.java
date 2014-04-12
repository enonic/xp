package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;

final class GetPartDescriptorCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorCommand>
{
    private PartDescriptorKey key;

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

    public GetPartDescriptorCommand key( final PartDescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
