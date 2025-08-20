package com.enonic.xp.core.impl.content;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;

public class ContentNodeTranslator
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private final NodeService nodeService;

    private final ContentDataSerializer contentDataSerializer;

    public ContentNodeTranslator( final NodeService nodeService )
    {
        this(nodeService, new ContentDataSerializer());
    }

    public ContentNodeTranslator( final NodeService nodeService, final ContentDataSerializer contentDataSerializer )
    {
        this.nodeService = nodeService;
        this.contentDataSerializer = contentDataSerializer;
    }

    public ContentDataSerializer getContentDataSerializer()
    {
        return contentDataSerializer;
    }

    public Contents fromNodes( final Nodes nodes, final boolean resolveHasChildren )
    {
        if ( resolveHasChildren )
        {
            return doTranslate( nodes, nodeService::hasChildren );
        }
        else
        {
            return doTranslate( nodes, n -> false );
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

    private Contents doTranslate( final Nodes nodes, final Function<Node, Boolean> hasChildrenFn )
    {
        final Contents.Builder contents = Contents.create();

        for ( final Node node : nodes )
        {
            try
            {
                contents.add( doTranslate( node, hasChildrenFn.apply( node ) ) );
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


    private Content doTranslate( final Node node, final boolean hasChildren )
    {
        return doTranslate( node, hasChildren, false );
    }

    private Content doTranslate( final Node node, final boolean hasChildren, final boolean allowAltRootPath )
    {
        final ContentId contentId = ContentId.from( node.id() );

        if ( !allowAltRootPath && !( node.path().toString().startsWith( ContentNodeHelper.getContentRoot() + "/" ) ||
            node.path().equals( ContentNodeHelper.getContentRoot() ) ) )
        {
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .build();
        }

        final Content.Builder<?> builder = contentDataSerializer.fromData( node.data().getRoot() );

        if ( node.parentPath().isRoot() )
        {
            builder.root();
        }
        else
        {
            builder.path( ContentNodeHelper.translateNodePathToContentPath( node.path() ) );
        }

        builder.id( contentId )
            .childOrder( node.getChildOrder() )
            .permissions( node.getPermissions() )
            .hasChildren( hasChildren )
            .manualOrderValue( node.getManualOrderValue() );

        return builder.build();
    }
}
