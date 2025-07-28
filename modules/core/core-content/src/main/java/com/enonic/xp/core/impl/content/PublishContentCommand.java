package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PublishContentCommand
    extends AbstractContentCommand
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
        final CompareContentResults results = getSyncWork();

        if ( publishContentListener != null )
        {
            publishContentListener.contentResolved( results.size() );
        }

        doPush( results.contentIds() );

        final PublishContentResult result = resultBuilder.build();

        if ( !result.getPushedContents().isEmpty() )
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

        if ( !checkIfAllContentsValid( ids ) )
        {
            this.resultBuilder.setFailed( ids );
            return;
        }
        doPushNodes( ContentNodeHelper.toNodeIds( ids ) );
    }

    private PushNodesResult doPushRoot()
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
        return nodeService.push( NodeIds.from( contentRootNode.id() ), ContentConstants.BRANCH_MASTER );
    }

    private CompareContentResults getSyncWork()
    {
        final Context context = ContextAccessor.current();

        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        return adminContext.callWith( ResolveContentsToBePublishedCommand.create()
                                          .contentIds( this.contentIds )
                                          .excludedContentIds( this.excludedContentIds )
                                          .excludeChildrenIds( this.excludeChildrenIds )
                                          .includeDependencies( this.includeDependencies )
                                          .contentTypeService( this.contentTypeService )
                                          .eventPublisher( this.eventPublisher )
                                          .translator( this.translator )
                                          .nodeService( this.nodeService )
                                          .build()::execute );
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
        setPublishInfo( nodesToPush );

        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, ContentConstants.BRANCH_MASTER, count -> {
            if ( publishContentListener != null )
            {
                publishContentListener.contentPushed( count );
            }
        } );

        commitPushedNodes( pushNodesResult.getSuccessful() );

        this.resultBuilder.setFailed( ContentNodeHelper.toContentIds( pushNodesResult.getFailedEntries()
                                                                          .stream()
                                                                          .map( failed -> failed.getNodeBranchEntry().getNodeId() )
                                                                          .collect( Collectors.toList() ) ) );
        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( pushNodesResult.getSuccessful().getKeys() ) );
    }

    private void setPublishInfo( final NodeIds nodesToPush )
    {
        final Instant publishFrom = contentPublishInfo.getFrom();
        final Instant publishTo = contentPublishInfo.getTo();

        for ( final NodeId id : nodesToPush )
        {
            this.nodeService.update( UpdateNodeParams.create().editor( toBeEdited -> {

                PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );

                if ( publishInfo == null )
                {
                    publishInfo = toBeEdited.data.addSet( ContentPropertyNames.PUBLISH_INFO );
                }

                final Instant currentPublishFirst = publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST );
                final Instant currentPublishFrom = publishInfo.getInstant( ContentPropertyNames.PUBLISH_FROM );

                if ( currentPublishFirst != null && currentPublishFrom != null )
                {
                    return;
                }

                if ( currentPublishFirst == null )
                {
                    if ( currentPublishFrom == null )
                    {
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_FIRST, publishFrom );
                    }
                    else
                    {
                        //TODO Special case for Enonic XP 6.7 and 6.8 contents. Remove after 7.0
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_FIRST, currentPublishFrom );
                    }
                }

                if ( currentPublishFrom == null )
                {
                    publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, publishFrom );
                    if ( publishTo == null )
                    {
                        if ( publishInfo.hasProperty( ContentPropertyNames.PUBLISH_TO ) )
                        {
                            publishInfo.removeProperty( ContentPropertyNames.PUBLISH_TO );
                        }
                    }
                    else
                    {
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_TO, publishTo );
                    }
                }

            } ).id( id ).build() );
            if ( publishContentListener != null )
            {
                publishContentListener.contentPushed( 1 );
            }
        }
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
            if ( contentPublishInfo == null )
            {
                this.contentPublishInfo = ContentPublishInfo.create().from( Instant.now() ).build();
            }
            else if ( contentPublishInfo.getFrom() == null )
            {
                this.contentPublishInfo = ContentPublishInfo.create().from( Instant.now() ).to( contentPublishInfo.getTo() ).build();
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
            ContentPublishInfoPreconditions.check( contentPublishInfo );
        }

        public PublishContentCommand build()
        {
            validate();
            return new PublishContentCommand( this );
        }

    }
}
