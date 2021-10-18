package com.enonic.xp.lib.content;


import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;

public final class ArchiveContentHandler
    extends BaseContextHandler
{
    private String content;

    @Override
    protected List<String> doExecute()
    {
        return executeArchive();
    }

    private List<String> executeArchive()

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
        }

        return archive( sourceId );
    }

    private List<String> archive( final ContentId sourceId )
    {
        final ArchiveContentParams archiveParams = ArchiveContentParams.create().contentId( sourceId ).build();

        final ArchiveContentsResult result = contentService.archive( archiveParams );

        return result.getArchivedContents().stream().map( ContentId::toString ).collect( Collectors.toList() );

    }

    public ArchiveContentHandler setContent( final String content )
    {
        this.content = content;
        return this;
    }
}
