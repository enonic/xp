package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;

public class ContentNodeTranslator
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private final NodeService nodeService;

    private final ContentDataSerializer contentDataSerializer;

    public ContentNodeTranslator( final NodeService nodeService, final ContentDataSerializer contentDataSerializer )
    {
        this.nodeService = nodeService;
        this.contentDataSerializer = contentDataSerializer;
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

    public Content fromNode( final Node node, final boolean resolveHasChildren )
    {
        return fromNode( node, resolveHasChildren, false );
    }

    public Content fromNode( final Node node, final boolean resolveHasChildren, final boolean allowAltRootPath )
    {
        final boolean hasChildren = resolveHasChildren && this.nodeService.hasChildren( node );
        return doTranslate( node, hasChildren, allowAltRootPath );
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
            catch ( final ContentNotFoundException e )
            {
                LOG.debug( "Failed to translate node '{}' [{}] to content", node.path(), node.id(), e );
            }
            catch ( final Exception e )
            {
                LOG.error( "Failed to translate node '{}' [{}] to content", node.path(), node.id(), e );
            }
        }

        return contents.build();
    }

    private ContentPath getParent( final NodePath nodePath )
    {
        if ( NodePath.ROOT.equals( nodePath.getParentPath() ) )
        {
            return ContentPath.ROOT;
        }

        return ContentNodeHelper.translateNodePathToContentPath( nodePath.getParentPath() );
    }

    private Content doTranslate( final Node node, final boolean hasChildren )
    {
        return doTranslate( node, hasChildren, false );
    }

    private Content doTranslate( final Node node, final boolean hasChildren, final boolean allowAltRootPath )
    {
        final ContentId contentId = ContentId.from( node.id() );

        if ( !allowAltRootPath && !( node.path().toString().startsWith( ContentNodeHelper.getContentRoot().toString() + "/" ) ||
            node.path().equals( ContentNodeHelper.getContentRoot() ) ) )
        {
            throw new ContentNotFoundException( contentId, ContextAccessor.current().getBranch(), ContentNodeHelper.getContentRoot() );
        }

        final ContentPath parentContentPath = getParent( node.path() );

        final Content.Builder<?> builder = contentDataSerializer.fromData( node.data().getRoot() );

        builder.id( contentId )
            .parentPath( parentContentPath )
            .name( node.name().toString() )
            .childOrder( node.getChildOrder() )
            .permissions( node.getPermissions() )
            .inheritPermissions( node.inheritsPermissions() )
            .hasChildren( hasChildren )
            .contentState( ContentState.from( node.getNodeState().value() ) )
            .manualOrderValue( node.getManualOrderValue() );

        final boolean isRoot = NodePath.ROOT.equals( node.parentPath() );

        if ( isRoot )
        {
            builder.root();
        }

        return builder.build();
    }
}
