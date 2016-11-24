package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.DeleteContentsParams;
import com.enonic.xp.context.ContextAccessor;

public final class DeleteContentHandler
    extends BaseContextHandler
{
    private String key;

    @Override
    protected Object doExecute()
    {
        if ( this.key.startsWith( "/" ) )
        {
            return deleteByPath( ContentPath.from( this.key ) );
        }
        else
        {
            return deleteById( ContentId.from( this.key ) );
        }
    }

    private boolean deleteById( final ContentId id )
    {
        try
        {
            final Content content = this.contentService.getById( id );
            return deleteByPath( content.getPath() );
        }
        catch ( final ContentNotFoundException e )
        {
            return false;
        }
    }

    private boolean deleteByPath( final ContentPath path )
    {
        final DeleteContentsParams params = DeleteContentsParams.create().
            contentPaths( ContentPaths.from( path ) ).
            deleteOnline( isMasterBranch() ).
            build();
        return doDelete( params );
    }

    private boolean doDelete( final DeleteContentsParams params )
    {
        try
        {
            return this.contentService.deleteWithoutFetch( params ) != null;
        }
        catch ( final ContentNotFoundException e )
        {
            return false;
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    private boolean isMasterBranch()
    {
        return ContextAccessor.current().getBranch().equals( ContentConstants.BRANCH_MASTER );
    }
}
