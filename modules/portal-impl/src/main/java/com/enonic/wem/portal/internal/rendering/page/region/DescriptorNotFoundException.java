package com.enonic.wem.portal.internal.rendering.page.region;


import com.enonic.wem.api.content.page.DescriptorKey;

public class DescriptorNotFoundException
    extends RuntimeException
{
    public DescriptorNotFoundException( final DescriptorKey descriptor )
    {
        super( "Descriptor not found: " + descriptor );
    }
}
