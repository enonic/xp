package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.TemplateKey;

public class TemplateNotFoundException
    extends RuntimeException
{
    public TemplateNotFoundException( final TemplateKey template, NotFoundException e )
    {
        super( "Template not found: " + template, e );
    }
}
