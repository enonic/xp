package com.enonic.wem.api.content.page;


import com.enonic.wem.api.exception.NotFoundException;

public class PageDescriptorNotFoundException
    extends NotFoundException
{
    public PageDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PageDescriptor [" + key.toString() + "] not found" );
    }
}
