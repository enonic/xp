package com.enonic.wem.portal.content;

import com.enonic.wem.portal.AbstractPortalException;

public class ContentNotFoundException
    extends AbstractPortalException
{

    public ContentNotFoundException( final String message )
    {
        super( message );
    }
}
