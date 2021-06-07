package com.enonic.xp.lib.content;


import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;

import static com.google.common.base.Strings.nullToEmpty;

public final class RestoreContentHandler
    extends BaseContextHandler
{
    private String content;

    private String path;

    @Override
    protected Object doExecute()
    {
        return executeRestore();
    }

    private boolean executeRestore()

    {
        final ContentId sourceId;
        final ContentPath sourcePath;
        if ( this.content.startsWith( "/" ) )
        {
            // source is path
            sourcePath = ContentPath.from( this.content );
            final Content sourceContent = contentService.getByPath( sourcePath );
            sourceId = sourceContent.getId();
        }
        else
        {
            // source is key
            sourceId = ContentId.from( this.content );
            final Content sourceContent = contentService.getById( sourceId );
            sourcePath = sourceContent.getPath();
        }

        final ContentPath contentPath = nullToEmpty( path ).isBlank() ? null : ContentPath.from( path );
        return restore( sourceId, contentPath );
    }

    private boolean restore( final ContentId sourceId, final ContentPath contentPath )
    {
        final RestoreContentParams restoreParams = RestoreContentParams.create().
            contentId( sourceId ).
            path( contentPath ).
            build();

        final RestoreContentsResult result = contentService.restore( restoreParams );

        return result.getRestoredContents().contains( sourceId );
    }

    public RestoreContentHandler setContent( final String content )
    {
        this.content = content;
        return this;
    }

    public RestoreContentHandler setPath( final String path )
    {
        this.path = path;
        return this;
    }
}
