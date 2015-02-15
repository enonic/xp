package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.content.page.PageDescriptorNotFoundException;
import com.enonic.xp.resource.ResourceNotFoundException;

final class GetPageDescriptorCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorCommand>
{
    private DescriptorKey key;

    public PageDescriptor execute()
    {
        try
        {
            return getDescriptor( this.key );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PageDescriptorNotFoundException( this.key, e );
        }
    }

    public GetPageDescriptorCommand key( final DescriptorKey key )
    {
        this.key = key;
        return this;
    }
}
