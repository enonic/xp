package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.content.ContentNodeTranslator;
import com.enonic.wem.core.index.IndexService;

final class ContentDaoHandlerGet
    extends AbstractContentDaoHandler
{
    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    ContentDaoHandlerGet( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    Content findContentByPath( final ContentPath contentPath )
        throws RepositoryException
    {
        return doFindContent( contentPath );
    }

    Contents findContentsByPath( final ContentPaths contentPaths )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        for ( ContentPath contentPath : contentPaths )
        {
            final Content content = doFindContent( contentPath );
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
            final Content content = doFindContent( contentId );
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
        return doFindContent( contentId );
    }
}

