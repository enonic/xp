package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;


/**
 * TODO: Figure out how to handle the thrown RepositoryException from JCR.
 */
@Component
public class ContentDaoImpl
    implements ContentDao
{

    @Override
    public ContentId createContent( final Content content, final Session session )
    {
        try
        {
            return new CreateContentDaoHandler( session ).handle( content );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void updateContent( final Content content, final boolean createNewVersion, final Session session )
    {
        try
        {
            new UpdateContentDaoHandler( session ).handle( content, createNewVersion );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteContent( final ContentPath contentPath, final Session session )
    {
        try
        {
            new DeleteContentDaoHandler( session ).deleteContentByPath( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteContent( final ContentId contentId, final Session session )
    {
        try
        {
            new DeleteContentDaoHandler( session ).deleteContentById( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void renameContent( final ContentPath contentPath, final String newName, final Session session )
    {
        try
        {
            new RenameContentDaoHandler( session ).handle( contentPath, newName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content findContent( final ContentPath contentPath, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).findContentByPath( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content findContent( final ContentId contentId, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).findContentById( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents findContents( final ContentPaths contentPaths, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).findContentsByPath( contentPaths );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents findContents( final ContentIds contentIds, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).findContentsById( contentIds );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents findChildContent( final ContentPath parentPath, final Session session )
    {
        try
        {
            return new FindChildContentDaoHandler( session ).handle( parentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public ContentTree getContentTree( final Session session )
    {
        try
        {
            return new GetContentTreeContentDaoHandler( session ).handle();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public int countContentTypeUsage( final QualifiedContentTypeName qualifiedContentTypeName, Session session )
    {
        try
        {
            return new CountContentTypeUsageDaoHandler( session ).handle( qualifiedContentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<ContentVersion> getContentVersions( final ContentPath contentPath, final Session session )
    {
        try
        {
            return new GetContentVersionHistoryDaoHandler( session ).handle( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<ContentVersion> getContentVersions( final ContentId contentId, final Session session )
    {
        try
        {
            return new GetContentVersionHistoryDaoHandler( session ).handle( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content getContentVersion( final ContentId contentId, final ContentVersionId versionId, final Session session )
    {
        try
        {
            return new GetContentVersionDaoHandler( session ).handle( contentId, versionId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content getContentVersion( final ContentPath path, final ContentVersionId versionId, final Session session )
    {
        try
        {
            return new GetContentVersionDaoHandler( session ).handle( path, versionId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
