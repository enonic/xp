package com.enonic.xp.content.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.exception.NotFoundException;

@Beta
public class LayoutDescriptorNotFoundException
    extends NotFoundException
{
    public LayoutDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "LayoutDescriptor [" + key.toString() + "] not found" );
    }
}