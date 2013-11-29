package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.exception.BaseException;

public final class PartTemplateNotFoundException
    extends BaseException
{
    public PartTemplateNotFoundException( final PartTemplateKey key )
    {
        super( "PartTemplate [{0}] was not found", key );
    }
}
