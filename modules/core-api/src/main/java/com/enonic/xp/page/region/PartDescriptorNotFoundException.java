package com.enonic.xp.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.page.DescriptorKey;


@Beta
public class PartDescriptorNotFoundException
    extends NotFoundException
{
    public PartDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PartDescriptor [" + key.toString() + "] not found" );
    }
}