package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class ContentNodeTranslator
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

    public ContentDataSerializer getContentDataSerializer()
    {
        return contentDataSerializer;
    }

    public Contents fromNodes( final Nodes nodes )
    {
        return doTranslate( nodes );
    }

    public Content fromNode( final Node node )
    {
        return doTranslate( node, false );
    }

    public Content fromNodeWithAnyRootPath( final Node node )
    {
        return doTranslate( node, true );
    }

    private Contents doTranslate( final Nodes nodes )
    {
        final Contents.Builder contents = Contents.create();

        for ( final Node node : nodes )
        {
            try
            {
                contents.add( doTranslate( node, false ) );
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


    private Content doTranslate( final Node node, final boolean allowAltRootPath )
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
            .manualOrderValue( node.getManualOrderValue() );

        return builder.build();
    }
}
