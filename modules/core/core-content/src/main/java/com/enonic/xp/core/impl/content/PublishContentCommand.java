package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;

public class PublishContentCommand
    extends AbstractContentCommand
    implements PushNodesListener
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeDependencies;

    private final PublishContentResult.Builder resultBuilder;

    private final PushContentListener publishContentListener;

    private final String message;

    private PublishContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.contentPublishInfo = builder.contentPublishInfo;
        this.includeDependencies = builder.includeDependencies;
        this.excludeChildrenIds = builder.excludeChildrenIds;
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
        this.nodeService.refresh( RefreshMode.ALL );

        final CompareContentResults results = getSyncWork();

        if ( publishContentListener != null )
        {
            publishContentListener.contentResolved( results.size() );
        }

        doPush( results.contentIds() );

        this.nodeService.refresh( RefreshMode.ALL );

        return resultBuilder.build();
    }

    private void doPush( final ContentIds ids )
    {
        final boolean validContents = checkIfAllContentsValid( ids );

        if ( validContents )
        {
            doPushNodes( NodeIds.from( ids.asStrings() ) );
        }
        else
        {
            this.resultBuilder.setFailed( ids );
        }
    }

    private CompareContentResults getSyncWork()
    {
        return ResolveContentsToBePublishedCommand.create()
            .contentIds( this.contentIds )
            .excludedContentIds( this.excludedContentIds )
            .excludeChildrenIds( this.excludeChildrenIds )
            .includeDependencies( this.includeDependencies )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .translator( this.translator )
            .nodeService( this.nodeService )
            .build()
            .execute();
    }

    private boolean checkIfAllContentsValid( final ContentIds pushContentsIds )
    {
        final ContentValidityResult result = CheckContentValidityCommand.create()
            .translator( this.translator )
            .nodeService( this.nodeService )
            .eventPublisher( this.eventPublisher )
            .contentTypeService( this.contentTypeService )
            .contentIds( pushContentsIds )
            .build()
            .execute();

        return result.allValid();
    }

    private void doPushNodes( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return;
        }

        SetPublishInfoCommand.create( this )
            .nodeIds( nodesToPush )
            .contentPublishInfo( contentPublishInfo )
            .pushListener( publishContentListener )
            .build()
            .execute();

        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, ContentConstants.BRANCH_MASTER, this );

        commitPushedNodes( pushNodesResult.getSuccessful() );

        this.resultBuilder.setFailed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getFailed()
                                                                                        .stream()
                                                                                        .map( failed -> failed.getNodeBranchEntry()
                                                                                            .getNodeId() )
                                                                                        .collect( Collectors.toList() ) ) ) );
        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getSuccessful().getKeys() ) ) );
    }

    private void commitPushedNodes( final NodeBranchEntries branchEntries )
    {
        final String commitEntryMessage = message == null
            ? ContentConstants.PUBLISH_COMMIT_PREFIX
            : String.join( ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER, ContentConstants.PUBLISH_COMMIT_PREFIX, message );

        final NodeCommitEntry commitEntry = NodeCommitEntry.create().message( commitEntryMessage ).build();
        final RoutableNodeVersionIds.Builder routableNodeVersionIds = RoutableNodeVersionIds.create();
        for ( NodeBranchEntry branchEntry : branchEntries )
        {
            final RoutableNodeVersionId routableNodeVersionId =
                RoutableNodeVersionId.from( branchEntry.getNodeId(), branchEntry.getVersionId() );
            routableNodeVersionIds.add( routableNodeVersionId );
        }
        nodeService.commit( commitEntry, routableNodeVersionIds.build() );
    }


    @Override
    public void nodesPushed( final int count )
    {
        if ( publishContentListener != null )
        {
            publishContentListener.contentPushed( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private ContentPublishInfo contentPublishInfo;

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

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public Builder contentPublishInfo( final ContentPublishInfo contentPublishInfo )
        {
            if ( contentPublishInfo != null && contentPublishInfo.getFrom() == null )
            {
                this.contentPublishInfo = ContentPublishInfo.create().from( Instant.now() ).to( contentPublishInfo.getFrom() ).build();
            }
            else
            {
                this.contentPublishInfo = contentPublishInfo;
            }
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
            Preconditions.checkNotNull( contentIds );
            if ( contentPublishInfo != null )
            {
                final Instant publishToInstant = contentPublishInfo.getTo();
                if ( publishToInstant != null )
                {
                    final Instant publishFromInstant = contentPublishInfo.getFrom();
                    Preconditions.checkArgument( publishToInstant.compareTo( publishFromInstant ) >= 0,
                                                 "'Publish to' must be set after 'Publish from'." );
                }
            }
        }

        public PublishContentCommand build()
        {
            validate();
            return new PublishContentCommand( this );
        }

    }
}
