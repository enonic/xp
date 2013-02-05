package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_PREFIX;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;

final class ContentDaoHandlerGetContentVersionHistory
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerGetContentVersionHistory( final Session session )
    {
        super( session );
    }

    List<ContentVersion> handle( final ContentPath contentPath )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentPath );
        return getContentVersions( contentNode );
    }

    List<ContentVersion> handle( final ContentId contentId )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentId );
        return getContentVersions( contentNode );
    }

    private List<ContentVersion> getContentVersions( final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionNode = getContentVersionHistoryNode( contentNode );
        final NodeIterator versionNodes = contentVersionNode.getNodes( CONTENT_VERSION_PREFIX + "*" );

        final ContentId contentId = ContentIdFactory.from( contentNode );

        final List<ContentVersion> contentVersionList = Lists.newArrayList();
        while ( versionNodes.hasNext() )
        {
            final Node versionNode = versionNodes.nextNode();
            final ContentVersion.Builder contentVersionBuilder = getContentVersion( versionNode );
            contentVersionBuilder.contentId( contentId );
            final ContentVersion contentVersion = contentVersionBuilder.build();
            contentVersionList.add( contentVersion );
        }
        return contentVersionList;
    }

    private ContentVersion.Builder getContentVersion( final Node versionNode )
        throws RepositoryException
    {
        final ContentVersion.Builder contentVersion = ContentVersion.newContentVersion();
        contentVersion.createdTime( getPropertyDateTime( versionNode, ContentJcrMapper.MODIFIED_TIME ) );
        if ( versionNode.hasProperty( ContentJcrMapper.MODIFIER ) )
        {
            contentVersion.creator( AccountKey.from( getPropertyString( versionNode, ContentJcrMapper.MODIFIER ) ).asUser() );
        }
        contentVersion.createdTime( getPropertyDateTime( versionNode, ContentJcrMapper.MODIFIED_TIME ) );
        if ( versionNode.hasProperty( ContentJcrMapper.VERSION_ID ) )
        {
            contentVersion.versionId( ContentVersionId.of( getPropertyLong( versionNode, ContentJcrMapper.VERSION_ID ) ) );
        }
        return contentVersion;
    }

}