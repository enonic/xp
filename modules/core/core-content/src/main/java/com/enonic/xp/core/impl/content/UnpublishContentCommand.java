package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public UnpublishContentsResult execute()
    {
        final Context context = ContextAccessor.current();

        final Context unpublishContext = ContextBuilder.from( context ).
            branch( params.getUnpublishBranch() ).
            build();

        return unpublishContext.callWith( this::unpublish );
    }

    private UnpublishContentsResult unpublish()
    {
        final ContentIds.Builder contentBuilder = ContentIds.create();

        for ( final ContentId contentId : this.params.getContentIds() )
        {
            recursiveUnpublish( NodeId.from( contentId ), this.params.isIncludeChildren(), contentBuilder );
        }

        final ContentIds contentIds = contentBuilder.build();
        final UnpublishContentsResult.Builder resultBuilder = UnpublishContentsResult.create().addUnpublished( contentIds );

        if ( contentIds.getSize() == 1 )
        {
            final Context draftContext = ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_DRAFT ).
                build();

            draftContext.callWith( () -> {
                resultBuilder.setContentName( this.getContent( contentIds.first() ).getDisplayName() );
                return null;
            } );
        }

        final UnpublishContentsResult result = resultBuilder.build();

        removePendingDeleteFromDraft( result );

        return result;
    }

    private void recursiveUnpublish( final NodeId nodeId, boolean includeChildren, final ContentIds.Builder contentsBuilder )
    {
        if ( includeChildren )
        {
            final FindNodesByParentResult result =
                this.nodeService.findByParent( FindNodesByParentParams.create().parentId( nodeId ).build() );

            result.getNodeIds().forEach( ( id ) -> recursiveUnpublish( id, true, contentsBuilder ) );
        }
        final NodeIds nodes = this.nodeService.deleteById( nodeId );
        if ( nodes != null && nodes.isNotEmpty() )
        {
            contentsBuilder.add( ContentId.from( nodes.first().toString() ) );
        }
    }

    private void removePendingDeleteFromDraft( final UnpublishContentsResult result )
    {
        final Branch currentBranch = ContextAccessor.current().getBranch();
        if ( !currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            final Context draftContext = ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_DRAFT ).
                build();
            draftContext.callWith( () -> {
                final Nodes draftNodes = this.nodeService.getByIds( NodeIds.from( result.getUnpublishedContents().asStrings() ) );
                for ( final Node draftNode : draftNodes )
                {
                    if ( draftNode.getNodeState().value().equalsIgnoreCase( ContentState.PENDING_DELETE.toString() ) )
                    {
                        this.nodeService.deleteById( draftNode.id() );
                    }
                }
                return null;
            } );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UnpublishContentParams params;

        public Builder params( final UnpublishContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

