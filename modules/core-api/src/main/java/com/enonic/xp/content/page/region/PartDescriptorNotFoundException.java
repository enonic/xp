package com.enonic.xp.content.page.region;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.exception.NotFoundException;


public class PartDescriptorNotFoundException
    extends NotFoundException
{
    public PartDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PartDescriptor [" + key.toString() + "] not found" );
    }
}