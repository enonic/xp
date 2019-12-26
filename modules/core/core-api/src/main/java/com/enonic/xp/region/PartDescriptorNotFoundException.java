package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.page.DescriptorKey;


@PublicApi
public class PartDescriptorNotFoundException
    extends NotFoundException
{
    public PartDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PartDescriptor [" + key.toString() + "] not found" );
    }
}