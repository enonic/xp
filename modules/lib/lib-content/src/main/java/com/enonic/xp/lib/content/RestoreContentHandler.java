package com.enonic.xp.lib.content;


import java.util.List;
import java.util.stream.Collectors;

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
    protected List<String> doExecute()
    {
        return executeRestore();
    }

    private List<String> executeRestore()

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

        final ContentPath pathToRestore = nullToEmpty( path ).isBlank() ? null : ContentPath.from( path );

        return restore( sourceId, pathToRestore );
    }

    private List<String> restore( final ContentId sourceId, final ContentPath pathToRestore )
    {
        final RestoreContentParams restoreParams = RestoreContentParams.create().contentId( sourceId ).path( pathToRestore ).build();

        final RestoreContentsResult result = contentService.restore( restoreParams );

        return result.getRestoredContents().stream().map( ContentId::toString ).collect( Collectors.toList() );
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
