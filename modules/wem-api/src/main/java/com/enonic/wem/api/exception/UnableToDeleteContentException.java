package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public final class UnableToDeleteContentException
    extends BaseException
{
    public UnableToDeleteContentException( final ContentPath path, final String reason )
    {
        super( "Not able to delete content with path [{0}]: " + reason, path.toString() );
    }

    public UnableToDeleteContentException( final ContentId contentId, final String reason )
    {
        super( "Not able to delete content with id [{0}]: " + reason, contentId.toString() );
    }
}
