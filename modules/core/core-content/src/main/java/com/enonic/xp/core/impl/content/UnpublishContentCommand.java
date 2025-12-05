package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public UnpublishContentsResult execute()
    {
        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();

        final Instant now = Instant.now();

        final NodeIds.Builder allDeletedBuilder = NodeIds.create();
        masterContext.runWith( () -> {
            for ( final ContentId contentId : this.params.getContentIds() )
            {
                final DeleteNodeResult deleteNodeResult =
                    this.nodeService.delete( DeleteNodeParams.create().nodeId( NodeId.from( contentId ) ).build() );

                final NodeIds nodeIds = deleteNodeResult.getNodeIds();
                if ( !nodeIds.isEmpty() )
                {
                    allDeletedBuilder.addAll( nodeIds );

                    if ( params.getPublishContentListener() != null )
                    {
                        params.getPublishContentListener().contentPushed( nodeIds.getSize() );
                    }
                }

                for ( DeleteNodeResult.Result deleted : deleteNodeResult.getDeleted() )
                {
                    nodeService.applyVersionAttributes( ApplyVersionAttributesParams.create()
                                                            .nodeVersionId( deleted.nodeVersionId() )
                                                            .addAttributes(
                                                                ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.UNPUBLISH_ATTR ) )
                                                            .build() );
                }
            }
        } );

        final NodeIds allDeleted = allDeletedBuilder.build();

        removePublishInfoAndCommit( allDeleted, now );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final UnpublishContentsResult.Builder resultBuilder =
            UnpublishContentsResult.create().addUnpublished( ContentNodeHelper.toContentIds( allDeleted ) );

        return resultBuilder.build();

    }

    private void removePublishInfoAndCommit( final NodeIds deleteNodeResult, Instant now )
    {
        for ( final var deleted : deleteNodeResult )
        {
            final Node updated = nodeService.update( UpdateNodeParams.create().id( deleted ).editor( toBeEdited -> {
                PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );
                if ( publishInfo != null )
                {
                    publishInfo.removeProperties( ContentPropertyNames.PUBLISH_FROM );
                    publishInfo.removeProperties( ContentPropertyNames.PUBLISH_TO );
                    if ( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST ).isAfter( now ) )
                    {
                        publishInfo.removeProperties( ContentPropertyNames.PUBLISH_FIRST );
                    }
                }
            } ).build() );

            nodeService.commit( CommitNodeParams.create()
                                    .nodeCommitEntry( NodeCommitEntry.create().message( ContentConstants.UNPUBLISH_COMMIT_PREFIX ).build() )
                                    .nodeVersionIds( NodeVersionIds.from( updated.getNodeVersionId() ) )
                                    .build() );
        }
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
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

