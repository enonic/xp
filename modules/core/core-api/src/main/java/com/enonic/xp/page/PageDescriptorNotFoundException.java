package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public class PageDescriptorNotFoundException
    extends NotFoundException
{
    public PageDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PageDescriptor [" + key.toString() + "] not found" );
    }
}
