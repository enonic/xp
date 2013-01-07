package com.enonic.wem.core.content;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.google.common.primitives.Longs;

import com.enonic.wem.api.command.content.GetContentVersionHistory;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionHistory;
import com.enonic.wem.core.command.CommandContext;

@Component
public class GetContentVersionHistoryHandler
    extends AbstractContentHandler<GetContentVersionHistory>
{
    private final ContentVersionComparator contentVersionComparator = new ContentVersionComparator();

    public GetContentVersionHistoryHandler()
    {
        super( GetContentVersionHistory.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentVersionHistory command )
        throws Exception
    {
        final ContentSelector selector = command.getSelector();
        final List<ContentVersion> contentVersionList = getContentVersions( selector, context.getJcrSession() );
        Collections.sort( contentVersionList, contentVersionComparator );

        final ContentVersionHistory contentVersionHistory = ContentVersionHistory.from( contentVersionList );
        command.setResult( contentVersionHistory );
    }

    private List<ContentVersion> getContentVersions( final ContentSelector selector, final Session session )
    {
        final List<ContentVersion> contentVersions;
        if ( selector instanceof ContentPath )
        {
            final ContentPath path = (ContentPath) selector;
            contentVersions = contentDao.getContentVersions( path, session );
        }
        else if ( selector instanceof ContentId )
        {
            final ContentId contentId = (ContentId) selector;
            contentVersions = contentDao.getContentVersions( contentId, session );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported content selector: " + selector.getClass().getCanonicalName() );
        }
        return contentVersions;
    }

    private class ContentVersionComparator
        implements Comparator<ContentVersion>
    {
        @Override
        public int compare( final ContentVersion contentVersion1, final ContentVersion contentVersion2 )
        {
            final long v1 = contentVersion1.getVersionId().id();
            final long v2 = contentVersion2.getVersionId().id();
            return Longs.compare( v1, v2 );
        }
    }
}
