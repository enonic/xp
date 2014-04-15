package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.api.content.page.PageTemplateKey;

public class PageTemplateNotFoundException
    extends RuntimeException
{
    public PageTemplateNotFoundException( final PageTemplateKey template, NotFoundException e )
    {
        super( "PageTemplate not found: " + template, e );
    }
}
