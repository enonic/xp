package com.enonic.xp.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.page.DescriptorKey;

@Beta
public class LayoutDescriptorNotFoundException
    extends NotFoundException
{
    public LayoutDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "LayoutDescriptor [" + key.toString() + "] not found" );
    }
}