package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.node.PushNodesResult;

public class PublishContentCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeDescendantsOf;

    private final Instant publishFrom;

    private final Instant publishTo;

    private final boolean includeDependencies;

    private final PublishContentResult.Builder resultBuilder;

    private final PushContentListener publishContentListener;

    private final String message;

    private PublishContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.publishFrom = Objects.requireNonNullElseGet( builder.publishFrom, Instant::now ).truncatedTo( ChronoUnit.MILLIS );
        this.publishTo = builder.publishTo != null ? builder.publishTo.truncatedTo( ChronoUnit.MILLIS ) : null;
        Preconditions.checkArgument( publishTo == null || publishTo.isAfter( publishFrom ), "publishTo must be after publishFrom" );
        this.includeDependencies = builder.includeDependencies;
        this.excludeDescendantsOf = builder.excludeDescendantsOf;
        this.resultBuilder = PublishContentResult.create();
        this.publishContentListener = builder.publishContentListener;
        this.message = builder.message;
    }

    public static Builder create()
    {
        return new Builder();
    }

    PublishContentResult execute()
    {
        final CompareContentResults results = getSyncWork();

        if ( publishContentListener != null )
        {
            publishContentListener.contentResolved( results.size() );
        }

        doPush( results.contentIds() );

        final PublishContentResult result = resultBuilder.build();

        if ( !result.getPublished().isEmpty() )
        {
            runAsAdmin( this::doPushRoot );
        }
        return result;
    }

    private void doPush( final ContentIds ids )
    {
        if ( ids.isEmpty() )
        {
            return;
        }

        final ContentValidityResult contentValidityResult = checkIfAllContentsValid( ids );
        if ( !contentValidityResult.allValid() )
        {
            contentValidityResult.getNotValidContentIds()
                .stream()
                .map( c -> PublishContentResult.Result.failure( c, PublishContentResult.Reason.INVALID ) )
                .forEach( resultBuilder::add );
            contentValidityResult.getNotReadyContentIds()
                .stream()
                .map( c -> PublishContentResult.Result.failure( c, PublishContentResult.Reason.NOT_READY ) )
                .forEach( resultBuilder::add );
            return;
        }

        final NodeIds nodesToPush = ContentNodeHelper.toNodeIds( ids );
        final PushNodeParams.Builder pushNodeParams =
            PushNodeParams.create().ids( nodesToPush ).processor( setPublishInfo() ).target( ContentConstants.BRANCH_MASTER );
        if ( publishContentListener != null )
        {
            pushNodeParams.publishListener( publishContentListener::contentPushed );
        }

        final PushNodesResult pushNodesResult = nodeService.push( pushNodeParams.build() );

        commit( pushNodesResult.getSuccessful() );

        final Attributes publishAttr = ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.PUBLISH_ATTR );

        for ( var pushNodeResult : pushNodesResult.getSuccessful() )
        {
            nodeService.applyVersionAttributes( ApplyVersionAttributesParams.create()
                                                    .nodeVersionId( pushNodeResult.getNodeVersionId() )
                                                    .addAttributes( publishAttr )
                                                    .build() );
        }

        pushNodesResult.getFailed()
            .stream()
            .map( r -> PublishContentResult.Result.failure( ContentId.from( r.getNodeId() ),
                                                            Enum.valueOf( PublishContentResult.Reason.class,
                                                                          r.getFailureReason().toString() ) ) )
            .forEach( resultBuilder::add );

        pushNodesResult.getSuccessful()
            .stream()
            .map( r -> PublishContentResult.Result.success( ContentId.from( r.getNodeId() ) ) )
            .forEach( resultBuilder::add );
    }

    private PushNodesResult doPushRoot()
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
        final PushNodeParams pushNodeParams =
            PushNodeParams.create().ids( NodeIds.from( contentRootNode.id() ) ).target( ContentConstants.BRANCH_MASTER ).build();
        return nodeService.push( pushNodeParams );
    }

    private CompareContentResults getSyncWork()
    {
        return runAsAdmin( ResolveContentsToBePublishedCommand.create()
                               .contentIds( this.contentIds )
                               .excludedContentIds( this.excludedContentIds )
                               .excludeDescendantsOf( this.excludeDescendantsOf )
                               .includeDependencies( this.includeDependencies )
                               .contentTypeService( this.contentTypeService )
                               .eventPublisher( this.eventPublisher )
                               .nodeService( this.nodeService )
                               .build()::execute );
    }

    private ContentValidityResult checkIfAllContentsValid( final ContentIds pushContentsIds )
    {
        return CheckContentValidityCommand.create()
            .nodeService( this.nodeService )
            .eventPublisher( this.eventPublisher )
            .contentTypeService( this.contentTypeService )
            .contentIds( pushContentsIds )
            .build()
            .execute();
    }

    private NodeDataProcessor setPublishInfo()
    {
        return ( PropertyTree data, NodePath nodePath ) -> {
            var toBeEdited = data.copy();
            final PropertySet publishInfo = Objects.requireNonNullElseGet( toBeEdited.getSet( ContentPropertyNames.PUBLISH_INFO ),
                                                                           () -> toBeEdited.addSet( ContentPropertyNames.PUBLISH_INFO ) );

            if ( !publishInfo.hasProperty( ContentPropertyNames.PUBLISH_FROM ) )
            {
                publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, publishFrom );
            }

            if ( publishTo == null )
            {
                publishInfo.removeProperties( ContentPropertyNames.PUBLISH_TO );
            }
            else
            {
                publishInfo.setInstant( ContentPropertyNames.PUBLISH_TO, publishTo );
            }

            if ( !publishInfo.hasProperty( ContentPropertyNames.PUBLISH_FIRST ) )
            {
                publishInfo.setInstant( ContentPropertyNames.PUBLISH_FIRST, publishFrom );
            }
            return toBeEdited;
        };
    }

    private void commit( final List<PushNodeResult> branchEntries )
    {
        final String commitEntryMessage = message == null
            ? ContentConstants.PUBLISH_COMMIT_PREFIX
            : String.join( ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER, ContentConstants.PUBLISH_COMMIT_PREFIX, message );

        nodeService.commit( CommitNodeParams.create()
                                .nodeCommitEntry( NodeCommitEntry.create().message( commitEntryMessage ).build() )
                                .nodeVersionIds(
                                    branchEntries.stream().map( PushNodeResult::getNodeVersionId ).collect( NodeVersionIds.collector() ) )
                                .build() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeDescendantsOf;

        private Instant publishFrom;

        private Instant publishTo;

        private boolean includeDependencies = true;

        private PushContentListener publishContentListener;

        private String message;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( final ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeDescendantsOf( final ContentIds excludeDescendantsOf )
        {
            this.excludeDescendantsOf = excludeDescendantsOf;
            return this;
        }

        public Builder publishFrom( Instant publishFrom )
        {
            this.publishFrom = publishFrom;
            return this;
        }

        public Builder publishTo( Instant publishTo )
        {
            this.publishTo = publishTo;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public PublishContentCommand build()
        {
            validate();
            return new PublishContentCommand( this );
        }

    }
}
