package com.enonic.xp.content.page;


import com.enonic.xp.exception.NotFoundException;

public class PageDescriptorNotFoundException
    extends NotFoundException
{
    public PageDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PageDescriptor [" + key.toString() + "] not found" );
    }
}
