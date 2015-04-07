package com.enonic.xp.content;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

@Beta
public final class UnableToDeleteContentException
    extends RuntimeException
{
    public UnableToDeleteContentException( final ContentPath path, final String reason )
    {
        super( MessageFormat.format( "Not able to delete content with path [{0}]: " + reason, path.toString() ) );
    }

    public UnableToDeleteContentException( final ContentId contentId, final String reason )
    {
        super( MessageFormat.format( "Not able to delete content with id [{0}]: " + reason, contentId.toString() ) );
    }
}
