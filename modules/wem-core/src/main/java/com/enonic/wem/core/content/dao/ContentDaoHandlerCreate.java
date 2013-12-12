package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.Attachments;
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
        this.nodeName = content.getName().toString();

        final Content storedContent = storeAsContentInJcr( content );

        //storeContentAsNode( content );

        return storedContent;
    }

    private Content storeAsContentInJcr( final Content content )
        throws RepositoryException
    {
        validateForCreate( content );

        final ContentPath path = content.getPath();

        final Node root = session.getRootNode();
        final Node contentRootJcrNode = JcrHelper.getNodeOrNull( root, CONTENTS_ROOT_PATH );
        final Node newContentNode;

        if ( path.isRoot() )
        {
            throw new ContentAlreadyExistException( path );
        }
        else if ( path.elementCount() == 1 )
        {
            if ( contentRootJcrNode == null )
            {
                throw new ContentAlreadyExistException( path );
            }
            if ( contentRootJcrNode.hasNode( nodeName ) )
            {
                throw new ContentAlreadyExistException( path );
            }

            newContentNode = addContentToJcr( content, contentRootJcrNode, nodeName );
        }
        else if ( path.elementCount() == 1 )
        {
            final Node contentsNode = JcrHelper.getNodeOrNull( root, CONTENTS_ROOT_PATH );
            if ( contentsNode == null )
            {
                throw new ContentNotFoundException( ContentPath.ROOT );
            }
            if ( contentRootJcrNode.hasNode( nodeName ) )
            {
                throw new ContentAlreadyExistException( path );
            }

            newContentNode = addContentToJcr( content, contentRootJcrNode, nodeName );
        }
        else
        {
            final Node parentContentJcrNode = doGetContentNode( path.getParentPath() );

            if ( parentContentJcrNode == null )
            {
                throw new ContentNotFoundException( path.getParentPath() );
            }
            else if ( parentContentJcrNode.hasNode( nodeName ) )
            {
                throw new ContentAlreadyExistException( path );
            }

            final Node parentNode = content.isEmbedded() ? parentContentJcrNode.getNode( "__embedded" ) : parentContentJcrNode;

            newContentNode = addContentToJcr( content, parentNode, nodeName );
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

    private void validateForCreate( final Content content )
    {
        if ( content.getId() != null )
        {
            throw new IllegalArgumentException( "Attempt to create new content with assigned id: " + content.getId() );
        }

        if ( content.getPath().isRoot() )
        {
            throw new ContentAlreadyExistException( content.getPath() );
        }

    }

    private Node addContentToJcr( final Content content, final Node parentNode, final String nodeName )
        throws RepositoryException
    {
        final Node newContentNode = parentNode.addNode( nodeName, JcrConstants.CONTENT_NODETYPE );
        contentJcrMapper.toJcr( content, newContentNode );

        return newContentNode;
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
            rootDataSet( node.data() ).
            parent( node.parent().asAbsolute() ).
            entityIndexConfig( node.getEntityIndexConfig() ).
            attachments( Attachments.empty() ).
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
