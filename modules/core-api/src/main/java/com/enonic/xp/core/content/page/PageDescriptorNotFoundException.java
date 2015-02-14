package com.enonic.xp.core.content.page;


import com.enonic.xp.core.exception.NotFoundException;

public class PageDescriptorNotFoundException
    extends NotFoundException
{
    public PageDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PageDescriptor [" + key.toString() + "] not found" );
    }
}
