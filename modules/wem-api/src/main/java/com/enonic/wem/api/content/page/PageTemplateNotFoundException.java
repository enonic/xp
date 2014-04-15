package com.enonic.wem.api.content.page;

import com.enonic.wem.api.exception.NotFoundException;

public final class PageTemplateNotFoundException
    extends NotFoundException
{
    public PageTemplateNotFoundException( final PageTemplateKey key )
    {
        super( "PageTemplate [{0}] was not found", key );
    }
}
