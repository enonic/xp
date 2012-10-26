package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

class FindContentDaoHandler
    extends AbstractContentDaoHandler
{
    FindContentDaoHandler( final Session session )
    {
        super( session );
    }

    Content handle( final ContentPath contentPath )
        throws RepositoryException
    {
        return doFindContent( contentPath, session );
    }

    Contents handle( final ContentPaths contentPaths )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        for ( ContentPath contentPath : contentPaths )
        {
            final Content content = doFindContent( contentPath, session );
            if ( content != null )
            {
                contentsBuilder.add( content );
            }
        }
        return contentsBuilder.build();
    }
}

