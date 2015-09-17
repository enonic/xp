package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;

public class NodeToContentTranslator
{
    private final NodeService nodeService;

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

    private final static Logger LOG = LoggerFactory.getLogger( OldContentNodeTranslator.class );

    private NodeToContentTranslator( Builder builder )
    {
        this.nodeService = builder.nodeService;
    }

    public Contents fromNodes( final Nodes nodes, final boolean resolveHasChildren )
    {
        if ( resolveHasChildren )
        {
            final NodesHasChildrenResult nodesHasChildren = this.nodeService.hasChildren( nodes );
            return doTranslate( nodes, nodesHasChildren );
        }
        else
        {
            return doTranslate( nodes, NodesHasChildrenResult.empty() );
        }


    }

    private Contents doTranslate( final Nodes nodes, final NodesHasChildrenResult nodeHasChildren )
    {
        final Contents.Builder contents = Contents.create();

        for ( final Node node : nodes )
        {
            try
            {
                contents.add( doTranslate( node, nodeHasChildren.hasChild( node.id() ) ) );
            }
            catch ( final Exception e )
            {
                LOG.error( "Failed to translate node '" + node.path() + "' [" + node.id().toString() + "] to content", e );
            }
        }

        return contents.build();
    }

    public Content fromNode( final Node node )
    {
        return doTranslate( node, this.nodeService.hasChildren( node ) );
    }

    private Content doTranslate( final Node node, final boolean hasChildren )
    {
        final NodePath parentNodePath = node.path().getParentPath();
        final NodePath parentContentPathAsNodePath = translateToContentPath( node, parentNodePath );
        final ContentPath parentContentPath = ContentPath.from( parentContentPathAsNodePath.toString() );

        final Content.Builder builder = contentDataSerializer.fromData( node.data().getRoot() );
        builder.
            id( ContentId.from( node.id().toString() ) ).
            parentPath( parentContentPath ).
            name( node.name().toString() ).
            childOrder( node.getChildOrder() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() ).
            hasChildren( hasChildren ).
            contentState( ContentState.from( node.getNodeState().value() ) );

        return builder.build();
    }

    private NodePath translateToContentPath( final Node node, final NodePath parentNodePath )
    {
        return !ContentConstants.CONTENT_ROOT_PATH.equals( node.path() ) ? parentNodePath.removeFromBeginning(
            ContentConstants.CONTENT_ROOT_PATH ) : NodePath.ROOT;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeService nodeService;

        private ContentDataSerializer contentDataSerializer;

        private Builder()
        {
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder contentDataSerializer( final ContentDataSerializer contentDataSerializer )
        {
            this.contentDataSerializer = contentDataSerializer;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeService, "NodeService must be set" );
            Preconditions.checkNotNull( contentDataSerializer, "ContentDataSerializer must be set" );
        }

        public NodeToContentTranslator build()
        {
            return new NodeToContentTranslator( this );
        }
    }
}
