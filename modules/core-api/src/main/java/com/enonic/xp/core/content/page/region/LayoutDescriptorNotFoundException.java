package com.enonic.xp.core.content.page.region;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.exception.NotFoundException;


public class LayoutDescriptorNotFoundException
    extends NotFoundException
{
    public LayoutDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "LayoutDescriptor [" + key.toString() + "] not found" );
    }
}