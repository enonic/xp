package com.enonic.xp.core.impl.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorNotFoundException;
import com.enonic.wem.api.resource.ResourceNotFoundException;

final class GetPageDescriptorCommand
    extends AbstractGetPageDescriptorCommand
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
