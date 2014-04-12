package com.enonic.wem.core.content.page.part;

import java.io.IOException;

import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.util.Exceptions;

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
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving part descriptor [{0}]", this.key ).withCause( e );
        }
    }

    public GetPartDescriptorCommand key( final PartDescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
