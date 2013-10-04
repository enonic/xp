package com.enonic.wem.core.content;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import com.google.common.primitives.Longs;

import com.enonic.wem.api.command.content.GetContentVersionHistory;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionHistory;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentVersionHistoryHandler
    extends CommandHandler<GetContentVersionHistory>
{
    private ContentDao contentDao;

    private final ContentVersionComparator contentVersionComparator = new ContentVersionComparator();

    @Override
    public void handle( final GetContentVersionHistory command )
        throws Exception
    {
        final ContentSelector selector = command.getSelector();
        final List<ContentVersion> contentVersionList = contentDao.getContentVersions( selector, context.getJcrSession() );

        Collections.sort( contentVersionList, contentVersionComparator );

        final ContentVersionHistory contentVersionHistory = ContentVersionHistory.from( contentVersionList );
        command.setResult( contentVersionHistory );
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

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
