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
    private ContentIds ids;

    private ContentPaths paths;

    private Branch branch;

    public ContentNotFoundException( final ContentPath path, final Branch branch )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found in branch [{1}]", path.toString(), branch ) );

        this.paths = ContentPaths.from( path );
        this.branch = branch;
    }

    public ContentNotFoundException( final ContentPaths contentPaths, final Branch branch )
    {
        super( MessageFormat.format( "Contents with paths [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentPaths ),
                                     branch ) );
        this.paths = contentPaths;
        this.branch = branch;
    }

    public ContentNotFoundException( final ContentId contentId, final Branch branch )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found in branch [{1}]", contentId.toString(), branch ) );

        this.ids = ContentIds.from( contentId );
        this.branch = branch;
    }

    public ContentNotFoundException( final ContentIds contentIds, final Branch branch )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentIds ),
                                     branch ) );

        this.ids = contentIds;
        this.branch = branch;
    }

    public ContentIds getIds()
    {
        return ids;
    }

    public ContentPaths getPaths()
    {
        return paths;
    }

    public Branch getBranch()
    {
        return branch;
    }
}
