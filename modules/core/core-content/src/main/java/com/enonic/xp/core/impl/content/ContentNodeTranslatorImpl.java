package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;

@Component
public final class ContentNodeTranslatorImpl
    implements ContentNodeTranslator
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private NodeService nodeService;

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

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

    public Content fromNode( final Node node, final boolean resolveHasChildren )
    {
        final boolean hasChildren = resolveHasChildren && this.nodeService.hasChildren( node );
        return doTranslate( node, hasChildren );
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

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
