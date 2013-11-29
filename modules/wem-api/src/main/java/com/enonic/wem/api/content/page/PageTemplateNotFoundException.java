package com.enonic.wem.api.content.page;

import com.enonic.wem.api.exception.BaseException;

public final class PageTemplateNotFoundException
    extends BaseException
{
    public PageTemplateNotFoundException( final PageTemplateKey key )
    {
        super( "PageTemplate [{0}] was not found", key );
    }
}
