package com.enonic.xp.page;


import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public class PageDescriptorNotFoundException
    extends NotFoundException
{
    public PageDescriptorNotFoundException( final DescriptorKey key, final Throwable cause )
    {
        super( cause, "PageDescriptor [" + key.toString() + "] not found" );
    }
}
