package com.enonic.wem.api.content.page.image;

import com.enonic.wem.api.exception.BaseException;

public final class ImageTemplateNotFoundException
    extends BaseException
{
    public ImageTemplateNotFoundException( final ImageTemplateKey key )
    {
        super( "ImageTemplate [{0}] was not found", key );
    }
}
