package com.enonic.xp.lib.content;


import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;

import static com.google.common.base.Strings.nullToEmpty;

public final class ArchiveContentHandler
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
        final ArchiveContentParams archiveParams = ArchiveContentParams.create().
            contentId( sourceId ).
            build();

        final ArchiveContentsResult result = contentService.archive( archiveParams );

        return result.getArchivedContents().contains( sourceId );
    }

    public ArchiveContentHandler setContent( final String content )
    {
        this.content = content;
        return this;
    }
}
