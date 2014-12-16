package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.DescriptorKey;

public class DescriptorNotFoundException
    extends RuntimeException
{
    public DescriptorNotFoundException( final DescriptorKey descriptor )
    {
        super( "Descriptor not found: " + descriptor );
    }
}
