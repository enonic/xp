package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.TemplateKey;

public class DescriptorNotFoundException
    extends RuntimeException
{
    public DescriptorNotFoundException( final DescriptorKey descriptor )
    {
        super( "Descriptor not found: " + descriptor );
    }

    public DescriptorNotFoundException( final TemplateKey template, final DescriptorKey descriptor )
    {
        super( "Descriptor for template [" + template + "] not found: " + descriptor );
    }
}
