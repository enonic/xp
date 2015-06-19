package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;

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
        final DeleteContentParams params = DeleteContentParams.create().
            contentPath( path ).
            build();
        return doDelete( params );
    }

    private boolean doDelete( final DeleteContentParams params )
    {
        try
        {
            return this.contentService.delete( params ) != null;
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
}
