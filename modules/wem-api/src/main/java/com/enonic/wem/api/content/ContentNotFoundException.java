package com.enonic.wem.api.content;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;

public final class ContentNotFoundException
    extends NotFoundException
{
    public ContentNotFoundException( final ContentPath path )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found", path.toString() ) );
    }

    public ContentNotFoundException( final ContentPaths contentPaths )
    {
        super( MessageFormat.format( "Contents with paths [{0}] were not found", Joiner.on( ", " ).join( contentPaths ) ) );
    }

    public ContentNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found", contentId.toString() ) );
    }

    public ContentNotFoundException( final ContentIds contentIds )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found", Joiner.on( ", " ).join( contentIds ) ) );
    }

    public ContentNotFoundException( final EntityId entityId )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found", entityId.toString() ) );
    }

    public ContentNotFoundException( final NodePath nodePath )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found", nodePath.toString() ) );
    }
}
