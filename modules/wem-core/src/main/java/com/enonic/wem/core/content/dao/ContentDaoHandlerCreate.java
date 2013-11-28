package com.enonic.wem.core.content.dao;


import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.content.ContentNodeTranslator;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENTS_ROOT_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_ATTACHMENTS_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_EMBEDDED_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


final class ContentDaoHandlerCreate
    extends AbstractContentDaoHandler
{
    private String nodeName;

    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    ContentDaoHandlerCreate( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    Content handle( final Content content )
        throws RepositoryException
    {
        this.nodeName = this.resolveNodeName( content );

        final Content storedContent = storeAsContentInJcr( content );

        storeContentAsNode( content );

        return storedContent;
    }

    private Content storeAsContentInJcr( final Content content )
        throws RepositoryException
    {
        if ( content.getId() != null )
        {
            throw new IllegalArgumentException( "Attempt to create new content with assigned id: " + content.getId() );
        }
        final ContentPath path = content.getPath();

        final Node root = session.getRootNode();

        final Node newContentNode;
        final String spaceRootPath = CONTENTS_ROOT_PATH;

        if ( path.isRoot() )
        {
            throw new ContentAlreadyExistException( path );
        }
        else if ( path.elementCount() == 1 )
        {
            final Node contentsNode = JcrHelper.getNodeOrNull( root, spaceRootPath );
            if ( contentsNode == null )
            {
                throw new ContentNotFoundException( ContentPath.ROOT );
            }
            if ( contentsNode.hasNode( nodeName ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, contentsNode, nodeName );
        }
        else
        {
            final Node parentContentNode = doGetContentNode( path.getParentPath() );
            if ( parentContentNode == null )
            {
                throw new ContentNotFoundException( path.getParentPath() );
            }
            else if ( parentContentNode.hasNode( nodeName ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, parentContentNode, nodeName );
        }
        final Node contentVersionHistoryNode = createContentVersionHistory( content, newContentNode );
        addContentVersion( content, contentVersionHistoryNode );

        final Node attachmentsNode = newContentNode.addNode( CONTENT_ATTACHMENTS_NODE, NT_UNSTRUCTURED );

        if ( !content.isEmbedded() )
        {
            newContentNode.addNode( CONTENT_EMBEDDED_NODE, NT_UNSTRUCTURED );
        }

        return ContentJcrHelper.nodeToContent( newContentNode, contentJcrMapper );
    }

    private Node addContentToJcr( final Content content, final Node parentNode, final String nodeName )
        throws RepositoryException
    {
        final Node newContentNode = parentNode.addNode( nodeName, JcrConstants.CONTENT_NODETYPE );
        contentJcrMapper.toJcr( content, newContentNode );

        return newContentNode;
    }

    private String resolveNodeName( final Content content )
    {
        if ( content.isDraft() )
        {
            return "__draft__" + UUID.randomUUID().toString();
        }
        else
        {
            return content.getName();
        }
    }

    private void storeContentAsNode( final Content content )
    {
        final com.enonic.wem.api.entity.Node node = CONTENT_NODE_TRANSLATOR.toNode( content );

        if ( node.name() == null || node.path() == null )
        {
            // Space, skip that shit
            return;
        }

        final CreateNodeArguments createNodeArguments = CreateNodeArguments.newCreateNodeArgs().
            creator( UserKey.superUser() ).
            name( this.nodeName ).
            icon( node.icon() ).
            rootDataSet( node.data() ).
            parent( node.parent().asAbsolute() ).
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
