package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.exception.NotFoundException;


public class PartDescriptorNotFoundException
    extends NotFoundException
{
    public PartDescriptorNotFoundException( final PartDescriptorKey key, final Throwable cause )
    {
        super( cause, "PartDescriptor [" + key.toString() + "] not found" );
    }
}