package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.jcr.JcrHelper;


/**
 * TODO: Figure out how to handle the thrown RepositoryException from JCR.
 */
@Component
public class ContentDaoImpl
    implements ContentDao
{
    ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    @Override
    public void createContent( final Session session, final Content content )
    {
        try
        {
            final Node root;
            root = session.getRootNode();

            final Node contentsNode = root.getNode( CONTENTS_PATH );
            final ContentPath path = content.getPath();

            if ( path.numberOfElements() == 1 )
            {
                if ( contentsNode.hasNode( path.getName() ) )
                {
                    throw new IllegalArgumentException( "Content already exists: " + path );
                }
                addContentToJcr( content, contentsNode );
            }
            else
            {
                final Node parentContentNode = getContentNode( session, path.getParentPath() );
                addContentToJcr( content, parentContentNode );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content findContent( final Session session, final ContentPath contentPath )
    {
        return doFindContent( contentPath, session );
    }

    @Override
    public Contents findContent( final Session session, final ContentPaths contentPaths )
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        for ( ContentPath contentPath : contentPaths )
        {
            Content content = doFindContent( contentPath, session );
            if ( content != null )
            {
                contentsBuilder.add( content );
            }
        }
        return contentsBuilder.build();
    }

    private Content doFindContent( final ContentPath contentPath, final Session session )
    {
        try
        {
            final Node contentNode = getContentNode( session, contentPath );

            if ( contentNode == null )
            {
                return null;
            }

            final Content content = Content.create( contentPath );
            contentJcrMapper.toContent( contentNode, content );
            return content;
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    private Node getContentNode( final Session session, final ContentPath contentPath )
        throws RepositoryException
    {
        final String path = getNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    private String getNodePath( final ContentPath contentPath )
    {
        return CONTENTS_PATH + contentPath.toString();
    }

    private void addContentToJcr( final Content content, final Node parentContentNode )
        throws RepositoryException
    {
        final Node newContentNode = parentContentNode.addNode( content.getName() );
        contentJcrMapper.toJcr( content, newContentNode );
    }
}
