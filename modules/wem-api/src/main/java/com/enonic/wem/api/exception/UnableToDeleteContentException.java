package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentPath;

public final class UnableToDeleteContentException
    extends BaseException
{
    public UnableToDeleteContentException( final ContentPath path, final String reason )
    {
        super( "Not able to delete content [{0}]: " + reason, path.toString() );
    }
}
