package com.enonic.xp.content;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ContentNotFoundException
    extends NotFoundException
{
    public ContentNotFoundException( final ContentPath path, final BranchId branchId )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found in branch [{1}]", path.toString(), branchId ) );
    }

    public ContentNotFoundException( final ContentPaths contentPaths, final BranchId branchId )
    {
        super( MessageFormat.format( "Contents with paths [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentPaths ),
                                     branchId ) );
    }

    public ContentNotFoundException( final ContentId contentId, final BranchId branchId )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found in branch [{1}]", contentId.toString(), branchId ) );
    }

    public ContentNotFoundException( final ContentIds contentIds, final BranchId branchId )
    {
        super( MessageFormat.format( "Contents with ids [{0}] were not found in branch [{1}]", Joiner.on( ", " ).join( contentIds ),
                                     branchId ) );
    }
}
