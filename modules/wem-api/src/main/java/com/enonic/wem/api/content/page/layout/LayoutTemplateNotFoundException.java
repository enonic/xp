package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.exception.BaseException;

public final class LayoutTemplateNotFoundException
    extends BaseException
{
    public LayoutTemplateNotFoundException( final LayoutTemplateKey key )
    {
        super( "LayoutTemplate [{0}] was not found", key );
    }
}
