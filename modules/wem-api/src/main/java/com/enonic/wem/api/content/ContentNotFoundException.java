package com.enonic.wem.api.content;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.api.workspace.Workspace;

public final class ContentNotFoundException
    extends NotFoundException
{
    public ContentNotFoundException( final ContentPath path, final Workspace workspace )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found in workspace [{1}]", path.toString(), workspace ) );
    }

    public ContentNotFoundException( final ContentPaths contentPaths, final Workspace workspace )
    {
        super( MessageFormat.format( "Contents with paths [{0}] were not found in workspace [{1}]", Joiner.on( ", " ).join( contentPaths ),
                                     workspace ) );
    }

    public ContentNotFoundException( final ContentId contentId, final Workspace workspace )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found  in workspace [{1}]", contentId.toString(), workspace ) );
    }

    public ContentNotFoundException( final ContentIds contentIds, final Workspace workspace )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found in workspace [{1}]", Joiner.on( ", " ).join( contentIds ),
                                     workspace ) );
    }
}
