package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.jcr.JcrHelper;

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

            ContentPath path = content.getPath();
            String name = path.getElement( path.numberOfElements() - 1 );

            if ( path.numberOfElements() == 1 )
            {
                if ( contentsNode.hasNode( name ) )
                {
                    throw new IllegalArgumentException( "Content already exists: " + path );
                }
                final Node contentNode = contentsNode.addNode( name );
                contentJcrMapper.toJcr( content, contentNode );
            }
            else
            {
                // TODO
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
        final Node contentNode;
        try
        {
            contentNode = getContentNode( session, contentPath );

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
}
