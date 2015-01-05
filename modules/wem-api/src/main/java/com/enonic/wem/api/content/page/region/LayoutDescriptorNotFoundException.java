package com.enonic.wem.api.content.page.region;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.exception.NotFoundException;


public class LayoutDescriptorNotFoundException
    extends NotFoundException
{
    public LayoutDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "LayoutDescriptor [" + key.toString() + "] not found" );
    }
}