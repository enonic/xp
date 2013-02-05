package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

final class ContentDaoHandlerFind
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerFind( final Session session )
    {
        super( session );
    }

    Content findContentByPath( final ContentPath contentPath )
        throws RepositoryException
    {
        return doFindContent( contentPath, session );
    }

    Contents findContentsByPath( final ContentPaths contentPaths )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        for ( ContentPath contentPath : contentPaths )
        {
            Preconditions.checkArgument( contentPath.isAbsolute(), "Content path must be absolute: " + contentPath.toString() );
            final Content content = doFindContent( contentPath, session );
            if ( content != null )
            {
                contentsBuilder.add( content );
            }
        }
        return contentsBuilder.build();
    }

    Contents findContentsById( final ContentIds contentIds )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        for ( ContentId contentId : contentIds )
        {
            final Content content = doFindContent( contentId, session );
            if ( content != null )
            {
                contentsBuilder.add( content );
            }
        }
        return contentsBuilder.build();
    }

    Content findContentById( final ContentId contentId )
        throws RepositoryException
    {
        return doFindContent( contentId, session );
    }
}

