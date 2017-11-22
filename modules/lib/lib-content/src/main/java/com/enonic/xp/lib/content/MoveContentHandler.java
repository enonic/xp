package com.enonic.xp.lib.content;

import java.util.UUID;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.RenameContentParams;
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
        final ContentPath sourcePath;
        if ( this.source.startsWith( "/" ) )
        {
            // source is path
            sourcePath = ContentPath.from( this.source );
            final Content sourceContent = contentService.getByPath( sourcePath );
            sourceId = sourceContent.getId();
        }
        else
        {
            // source is key
            sourceId = ContentId.from( this.source );
            final Content sourceContent = contentService.getById( sourceId );
            sourcePath = sourceContent.getPath();
        }

        if ( target.endsWith( "/" ) )
        {
            // move as child of target path, keep same name
            return move( sourceId, ContentPath.from( target ).asAbsolute() );
        }
        else if ( !target.startsWith( "/" ) )
        {
            // just rename, keep same parent path
            return rename( sourceId, target );
        }
        else
        {
            // rename+move to target path
            final ContentPath targetPath = ContentPath.from( target );
            final ContentPath targetParent = targetPath.getParentPath();
            if ( targetParent.equals( sourcePath.getParentPath() ) )
            {
                // just rename, target path has same parent as source
                return rename( sourceId, targetPath.getName() );
            }

            if ( contentService.contentExists( targetPath ) )
            {
                throw new ContentAlreadyExistsException( targetPath );
            }

            // needs to be first renamed to temporary unique name to avoid clashing with siblings with same target name in source parent or with siblings with source name in target parent
            rename( sourceId, uniqueName() );

            move( sourceId, targetParent );
            return rename( sourceId, targetPath.getName() );
        }
    }

    private ContentMapper convert( final Content content )
    {
        return new ContentMapper( content );
    }

    private Content move( final ContentId sourceId, final ContentPath newPath )
    {
        final MoveContentParams moveParams = MoveContentParams.create().
            contentId( sourceId ).
            parentContentPath( newPath ).
            build();
        final MoveContentsResult result = contentService.move( moveParams );
        return contentService.getById( result.getMovedContents().first() );
    }

    private Content rename( final ContentId sourceId, final String newName )
    {
        final ContentName newContentName = ContentName.from( newName );
        final RenameContentParams renameParams = RenameContentParams.create().contentId( sourceId ).newName( newContentName ).build();
        return contentService.rename( renameParams );
    }

    private String uniqueName()
    {
        return UUID.randomUUID().toString();
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
