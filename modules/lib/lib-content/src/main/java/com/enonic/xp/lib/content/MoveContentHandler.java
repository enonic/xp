package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class MoveContentHandler
    extends BaseContextHandler
{
    private String source;

    private String target;

    @Override
    protected Object doExecute()
    {
        return convert( executeMove() );
    }

    private Content executeMove()
    {
        final ContentId sourceId;
        if ( this.source.startsWith( "/" ) )
        {
            // source is path
            sourceId = contentService.getByPath( ContentPath.from( this.source ) ).getId();
        }
        else
        {
            // source is key
            sourceId = ContentId.from( this.source );
        }

        if ( target.endsWith( "/" ) )
        {
            // move as child of target path, keep same name
            // /a/b -> /c/d/ => /c/d/b
            return renmov( sourceId, null, ContentPath.from( target ) );
        }
        else if ( !target.startsWith( "/" ) )
        {
            // just rename, keep same parent path
            // /a/b -> c => /a/c
            return renmov( sourceId, ContentName.from( target ), null );
        }
        else
        {
            // rename+move to target path
            final ContentPath targetPath = ContentPath.from( target );
            return renmov( sourceId, targetPath.getName(), targetPath.getParentPath() );
        }
    }

    private ContentMapper convert( final Content content )
    {
        return new ContentMapper( content );
    }

    private Content renmov( final ContentId sourceId, final ContentName newName, final ContentPath newPath )
    {
        final MoveContentParams moveParams =
            MoveContentParams.create().contentId( sourceId ).parentContentPath( newPath ).newName( newName ).build();
        final MoveContentsResult result = contentService.move( moveParams );
        return contentService.getById( result.getMovedContents().first() );
    }

    public void setSource( final String source )
    {
        this.source = source;
    }

    public void setTarget( final String target )
    {
        this.target = target;
    }
}
