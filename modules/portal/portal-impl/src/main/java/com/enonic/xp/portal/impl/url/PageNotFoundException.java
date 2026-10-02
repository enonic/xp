package com.enonic.xp.portal.impl.url;

import com.enonic.xp.exception.NotFoundException;

/**
 * A page URL that cannot be generated: the URL generator answers it with a 404 error URL, whatever the cause.
 */
final class PageNotFoundException
    extends NotFoundException
{
    PageNotFoundException( final Throwable cause )
    {
        super( cause, "Page URL does not resolve" );
    }
}
