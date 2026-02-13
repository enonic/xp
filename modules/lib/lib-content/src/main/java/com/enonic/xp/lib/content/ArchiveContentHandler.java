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
        return archive( getContentId( content ) );
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
