package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.content.ContentNodeTranslator;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_ATTACHMENTS_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_EMBEDDED_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.SPACES_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.SPACE_CONTENT_ROOT_NODE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


final class ContentDaoHandlerCreate
    extends AbstractContentDaoHandler
{
    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    private IndexService indexService;


    ContentDaoHandlerCreate( final Session session, final IndexService indexService )
    {
        super( session );
        this.indexService = indexService;
    }

    ContentId handle( final Content content )
        throws RepositoryException
    {
        if ( content.getId() != null )
        {
            throw new IllegalArgumentException( "Attempt to create new content with assigned id: " + content.getId() );
        }
        final ContentPath path = content.getPath();
        Preconditions.checkArgument( path.isAbsolute(), "Content path must be absolute: " + path.toString() );

        final Node root = session.getRootNode();
        final String spaceNodePath = SPACES_PATH + path.getSpace().name();
        if ( !root.hasNode( spaceNodePath ) )
        {
            throw new SpaceNotFoundException( path.getSpace() );
        }

        final Node newContentNode;
        final String spaceRootPath = getSpaceRootPath( path.getSpace() );
        if ( path.isRoot() )
        {
            if ( root.hasNode( spaceRootPath ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            final Node spaceNode = root.getNode( spaceNodePath );

            newContentNode = addContentToJcr( content, spaceNode );
        }
        else if ( path.elementCount() == 1 )
        {
            final Node contentsNode = JcrHelper.getNodeOrNull( root, spaceRootPath );
            if ( contentsNode == null )
            {
                throw new ContentNotFoundException( ContentPath.rootOf( path.getSpace() ) );
            }
            if ( contentsNode.hasNode( path.getName() ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, contentsNode );
        }
        else
        {
            final Node parentContentNode = doGetContentNode( path.getParentPath() );
            if ( parentContentNode == null )
            {
                throw new ContentNotFoundException( path.getParentPath() );
            }
            else if ( parentContentNode.hasNode( path.getName() ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, parentContentNode );
        }
        final Node contentVersionHistoryNode = createContentVersionHistory( content, newContentNode );
        addContentVersion( content, contentVersionHistoryNode );

        final Node attachmentsNode = newContentNode.addNode( CONTENT_ATTACHMENTS_NODE, NT_UNSTRUCTURED );

        if ( !content.isEmbedded() )
        {
            newContentNode.addNode( CONTENT_EMBEDDED_NODE, NT_UNSTRUCTURED );
        }

        return ContentIdFactory.from( newContentNode );
    }

    private Node addContentToJcr( final Content content, final Node parentNode )
        throws RepositoryException
    {
        final String nodeName = content.getName() == null ? SPACE_CONTENT_ROOT_NODE : content.getName();
        final Node newContentNode = parentNode.addNode( nodeName, JcrConstants.CONTENT_NODETYPE );
        contentJcrMapper.toJcr( content, newContentNode );

        storeAsNode( content );

        return newContentNode;
    }

    private void storeAsNode( final Content content )
    {
        final com.enonic.wem.api.entity.Node node = CONTENT_NODE_TRANSLATOR.toNode( content );

        if ( node.name() == null || node.path() == null )
        {
            // Space, skip that shit
            return;
        }

        final CreateNodeArguments createNodeArguments = CreateNodeArguments.newCreateNodeArgs().
            creator( UserKey.superUser() ).
            name( node.name() ).
            icon( node.icon() ).
            rootDataSet( node.data() ).
            parent( node.parent() ).
            entityIndexConfig( node.getEntityIndexConfig() ).
            build();

        final NodeJcrDao nodeJcrDao = new NodeJcrDao( this.session );

        final com.enonic.wem.api.entity.Node persistedNode = nodeJcrDao.createNode( createNodeArguments );

        indexService.indexNode( persistedNode );
    }

    private Node createContentVersionHistory( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionNode = contentNode.addNode( CONTENT_VERSION_HISTORY_NODE, NT_UNSTRUCTURED );
        contentVersionNode.setProperty( CONTENT_NEXT_VERSION_PROPERTY, content.getVersionId().id() + 1 );
        return contentVersionNode;
    }

}
