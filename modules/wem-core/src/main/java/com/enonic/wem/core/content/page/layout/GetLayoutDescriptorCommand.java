package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.util.Exceptions;

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
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving layout descriptor [{0}]", this.key ).withCause( e );
        }
    }

    public GetLayoutDescriptorCommand key( final LayoutDescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
