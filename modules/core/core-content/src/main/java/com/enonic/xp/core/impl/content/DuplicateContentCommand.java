package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.DuplicateContentException;
import com.enonic.xp.content.DuplicateContentListener;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.node.DuplicateNodeListener;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.DuplicateNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;

final class DuplicateContentCommand
    extends AbstractContentCommand
{
    private final DuplicateContentParams params;

    private DuplicateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final DuplicateContentParams params )
    {
        return new Builder( params );
    }

    DuplicateContentsResult execute()
    {
        try
        {
            return doExecute();
        }
        catch ( Exception e )
        {
            throw new DuplicateContentException( e.getMessage() );
        }
    }

    private DuplicateContentsResult doExecute()
    {
        final Node sourceNode = nodeService.getById( NodeId.from( params.getContentId() ) );

        final DuplicateNodeResult duplicatedNode = nodeService.duplicate( createDuplicateNodeParams( sourceNode ) );

        final Content duplicatedContent = ContentNodeTranslator.fromNode( duplicatedNode.getNode() );

        final ContentIds childrenIds = ContentNodeHelper.toContentIds( duplicatedNode.getChildren().getIds() );

        return DuplicateContentsResult.create()
            .setSourceContentPath( ContentNodeHelper.translateNodePathToContentPath( sourceNode.path() ) )
            .setContentName( duplicatedContent.getDisplayName() )
            .addDuplicated( duplicatedContent.getId() )
            .addDuplicated( childrenIds )
            .build();
    }

    private DuplicateNodeParams createDuplicateNodeParams( final Node sourceNode )
    {
        final boolean isVariant = sourceNode.data().getReference( ContentPropertyNames.VARIANT_OF ) != null;

        final NodeId sourceNodeId = ( !isVariant && params.isVariant() ) ? sourceNode.id() : null;

        final DuplicateNodeParams.Builder builder = DuplicateNodeParams.create()
            .nodeId( sourceNode.id() )
            .versionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.DUPLICATE_KEY ) )
            .dataProcessor( new DuplicateContentProcessor( params.getWorkflowInfo(), sourceNodeId ) )
            .refresh( RefreshMode.SEARCH );

        if ( params.getDuplicateContentListener() != null )
        {
            builder.duplicateListener( new ListenerDelegate( params.getDuplicateContentListener() ) );
        }

        builder.name( params.getName() );
        if ( params.getParent() != null )
        {
            builder.parent( ContentNodeHelper.translateContentPathToNodePath( params.getParent() ) );
        }
        builder.includeChildren( params.getIncludeChildren() );

        return builder.build();
    }

    static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final DuplicateContentParams params;

        Builder( final DuplicateContentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        DuplicateContentCommand build()
        {
            validate();
            return new DuplicateContentCommand( this );
        }
    }

    private static final class ListenerDelegate
        implements DuplicateNodeListener
    {
        DuplicateContentListener delegate;

        ListenerDelegate( DuplicateContentListener delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public void nodesDuplicated( final int count )
        {
            delegate.contentDuplicated( count );
        }

        @Override
        public void nodesReferencesUpdated( final int count )
        {
            delegate.contentReferencesUpdated( count );
        }
    }
}
