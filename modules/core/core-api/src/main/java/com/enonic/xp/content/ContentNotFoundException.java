package com.enonic.xp.content;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ContentNotFoundException
    extends NotFoundException
{
    private ContentPath path;

    public ContentNotFoundException( final ContentPath path, final Branch branch )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found in branch [{1}]", path.toString(), branch ) );
        this.path = path;
    }

    public ContentNotFoundException( final ContentPaths contentPaths, final Branch branch )
    {
        super( MessageFormat.format( "Contents with paths [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentPaths ),
                                     branch ) );
        this.path = path;
    }

    public ContentNotFoundException( final ContentId contentId, final Branch branch )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found in branch [{1}]", contentId.toString(), branch ) );
        this.path = path;
    }

    public ContentNotFoundException( final ContentIds contentIds, final Branch branch )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentIds ),
                                     branch ) );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
