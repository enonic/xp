package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.DuplicateContentException;
import com.enonic.xp.content.DuplicateContentListener;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.node.DuplicateNodeListener;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;

final class DuplicateContentCommand
    extends AbstractContentCommand
    implements DuplicateNodeListener
{
    private final DuplicateContentParams params;

    private final DuplicateContentListener duplicateContentListener;

    private DuplicateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.duplicateContentListener = builder.duplicateContentListener;
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

        final Node duplicatedNode = nodeService.duplicate( createDuplicateNodeParams( sourceNode ) );

        final Content duplicatedContent = translator.fromNode( duplicatedNode, true );

        final ContentIds childrenIds = params.getIncludeChildren() ? getAllChildren( duplicatedContent ) : ContentIds.empty();

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
            .duplicateListener( this )
            .nodeId( sourceNode.id() )
            .dataProcessor( new DuplicateContentProcessor( params.getWorkflowInfo(), sourceNodeId ) )
            .refresh( RefreshMode.SEARCH );

        builder.name( params.getName() );
        if ( params.getParent() != null )
        {
            builder.parent( ContentNodeHelper.translateContentPathToNodePath( params.getParent() ) );
        }
        builder.includeChildren( params.getIncludeChildren() );

        return builder.build();
    }

    @Override
    public void nodesDuplicated( final int count )
    {
        if ( duplicateContentListener != null )
        {
            duplicateContentListener.contentDuplicated( count );
        }
    }

    @Override
    public void nodesReferencesUpdated( final int count )
    {
        if ( duplicateContentListener != null )
        {
            duplicateContentListener.contentReferencesUpdated( count );
        }
    }

    private ContentIds getAllChildren( final Content duplicatedContent )
    {
        final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent(
            FindNodesByParentParams.create().parentId( NodeId.from( duplicatedContent.getId() ) ).recursive( true ).build() );

        return ContentNodeHelper.toContentIds( findNodesByParentResult.getNodeIds() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final DuplicateContentParams params;

        private DuplicateContentListener duplicateContentListener;

        Builder( final DuplicateContentParams params )
        {
            this.params = params;
        }

        public Builder duplicateListener( final DuplicateContentListener duplicateListener )
        {
            this.duplicateContentListener = duplicateListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public DuplicateContentCommand build()
        {
            validate();
            return new DuplicateContentCommand( this );
        }
    }

}
