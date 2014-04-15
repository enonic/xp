package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.exception.NotFoundException;


public class LayoutDescriptorNotFoundException
    extends NotFoundException
{
    public LayoutDescriptorNotFoundException( final LayoutDescriptorKey key, final Throwable cause )
    {
        super( cause, "LayoutDescriptor [" + key.toString() + "] not found" );
    }
}