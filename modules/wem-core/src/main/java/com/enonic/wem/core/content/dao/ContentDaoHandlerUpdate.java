package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.content.ContentNodeTranslator;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;

final class ContentDaoHandlerUpdate
    extends AbstractContentDaoHandler
{
    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    ContentDaoHandlerUpdate( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    Content handle( Content content, final boolean createNewVersion )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( content.getPath() );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content.getPath() );
        }

        content = increaseContentVersion( content, contentNode );
        contentJcrMapper.toJcr( content, contentNode );

        updateContentAsNode( content );

        if ( createNewVersion )
        {
            final Node contentVersionHistoryParent = getContentVersionHistoryNode( contentNode );
            addContentVersion( content, contentVersionHistoryParent );
        }

        return content;
    }

    private void updateContentAsNode( final Content content )
        throws RepositoryException
    {

        final NodeJcrDao nodeJcrDao = new NodeJcrDao( this.session );

        final NodeEditor nodeEditor = CONTENT_NODE_TRANSLATOR.toNodeEditor( content );

        final NodePath nodePathToContent = new NodePath( "/content" + content.getPath().toString() );

        final com.enonic.wem.api.entity.Node persisted = nodeJcrDao.getNodeByPath( nodePathToContent );

        final com.enonic.wem.api.entity.Node.EditBuilder editBuilder = nodeEditor.edit( persisted );

        if ( !editBuilder.isChanges() )

        {
            // TODO: set status NO CHANGE?
            return;
        }

        final com.enonic.wem.api.entity.Node edited = editBuilder.build();

        persisted.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            nodeToUpdate( persisted.id() ).
            name( edited.name() ).
            icon( edited.icon() ).
            rootDataSet( edited.data() ).
            build();

        final com.enonic.wem.api.entity.Node updatedNode = nodeJcrDao.updateNode( updateNodeArgs );

        session.save();

        indexService.indexNode( updatedNode );
    }

    private Content increaseContentVersion( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionParent = getContentVersionHistoryNode( contentNode );
        final ContentVersionId versionId = ContentVersionId.of( nextContentVersion( contentVersionParent ) );
        return Content.newContent( content ).version( versionId ).build();
    }

    private long nextContentVersion( final Node contentVersionParent )
        throws RepositoryException
    {
        final long versionNumber = getPropertyLong( contentVersionParent, CONTENT_NEXT_VERSION_PROPERTY, 0l );
        contentVersionParent.setProperty( CONTENT_NEXT_VERSION_PROPERTY, versionNumber + 1 );
        return versionNumber;
    }
}
