package com.enonic.xp.content;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
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
        super( MessageFormat.format( "Contents with paths [{0}] were not found in branch [{1}]",
                                     contentPaths.stream().map( Objects::toString ).collect( Collectors.joining( ", " ) ), branch ) );
    }

    public ContentNotFoundException( final ContentId contentId, final Branch branch )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found in branch [{1}]", contentId.toString(), branch ) );
    }

    public ContentNotFoundException( final ContentIds contentIds, final Branch branch )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found in branch [{1}]",
                                     contentIds.stream().map( Objects::toString ).collect( Collectors.joining( ", " ) ), branch ) );
    }

    public ContentNotFoundException( final ContentId contentId, final ContentVersionId versionId, final Branch branch )
    {
        super( MessageFormat.format( "Content with id [{0}] and versionId [{1}] was not found in branch [{2}]", contentId, versionId,
                                     branch ) );
    }

    public ContentNotFoundException( final ContentPath contentPath, final ContentVersionId versionId, final Branch branch )
    {
        super( MessageFormat.format( "Content with path [{0}] and versionId [{1}] was not found in branch [{2}]", contentPath, versionId,
                                     branch ) );
    }

    public ContentPath getPath()
    {
        return path;
    }
}
