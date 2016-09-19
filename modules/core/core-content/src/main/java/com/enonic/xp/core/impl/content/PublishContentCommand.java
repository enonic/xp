package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;

public class PublishContentCommand
    extends AbstractContentCommand
    implements PushNodesListener
{
    private final static PushContentListener NULL_LISTENER = new NullPushContentListener();

    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final Branch target;

    private final boolean resolveSyncWork;

    private final PublishContentResult.Builder resultBuilder;

    private final boolean includeChildren;

    private final PushContentListener pushContentListener;

    private PublishContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.resolveSyncWork = builder.includeDependencies;
        this.includeChildren = builder.includeChildren;
        this.resultBuilder = PublishContentResult.create();
        this.pushContentListener = builder.pushContentListener == null ? NULL_LISTENER : builder.pushContentListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    PublishContentResult execute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        final CompareContentResults results;
        if ( resolveSyncWork )
        {
            results = getSyncWork();
        }
        else
        {
            results = CompareContentsCommand.create().
                contentIds( this.contentIds ).
                nodeService( this.nodeService ).
                target( this.target ).
                build().
                execute();
        }
        pushContentListener.contentResolved( results.size() );
        pushAndDelete( results );

        this.nodeService.refresh( RefreshMode.ALL );

        return resultBuilder.build();
    }

    private void pushAndDelete( final CompareContentResults results )
    {
        NodeIds.Builder pushNodesIds = NodeIds.create();
        NodeIds.Builder deletedNodesIds = NodeIds.create();

        for ( CompareContentResult compareResult : results )
        {
            if ( compareResult.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                deletedNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
            else
            {
                pushNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
        }

        final ContentIds pushContentsIds = ContentIds.from( pushNodesIds.build().stream().
            map( ( n ) -> ContentId.from( n.toString() ) ).
            toArray( ContentId[]::new ) );

        final boolean validContents = checkIfAllContentsValid( pushContentsIds );

        if ( validContents )
        {
            doPushNodes( pushNodesIds.build() );
        }

        doDeleteNodes( deletedNodesIds.build() );
    }

    private CompareContentResults getSyncWork()
    {
        return ResolveContentsToBePublishedCommand.create().
            contentIds( this.contentIds ).
            excludedContentIds( this.excludedContentIds ).
            includeChildren( this.includeChildren ).
            target( this.target ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    private boolean checkIfAllContentsValid( final ContentIds pushContentsIds )
    {
        return CheckContentsValidCommand.create().
            translator( this.translator ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            contentTypeService( this.contentTypeService ).
            contentIds( pushContentsIds ).
            build().
            execute();
    }


    private void doPushNodes( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return;
        }

        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, this.target, this );

        this.resultBuilder.setFailed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getFailed().
            stream().map( failed -> failed.getNodeBranchEntry().getNodeId() ).collect( Collectors.toList() ) ) ) );
        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getSuccessful().getKeys() ) ) );
    }

    private void doDeleteNodes( final NodeIds nodeIdsToDelete )
    {
        final ContentIds contentIdsToDelete = ContentNodeHelper.toContentIds( NodeIds.from( nodeIdsToDelete ) );
        this.resultBuilder.setDeleted( contentIdsToDelete );

        final Context currentContext = ContextAccessor.current();
        deleteNodesInContext( nodeIdsToDelete, currentContext );
        deleteNodesInContext( nodeIdsToDelete, ContextBuilder.from( currentContext ).
            branch( target ).
            build() );

        for ( ContentId contentId : contentIdsToDelete )
        {
            pushContentListener.contentPushed( contentId, PushContentListener.PushResult.DELETED );
        }
    }

    private void deleteNodesInContext( final NodeIds nodeIds, final Context context )
    {
        context.callWith( () -> {
            nodeIds.forEach( nodeService::deleteById );
            return null;
        } );
    }

    @Override
    public void nodePushed( final NodeId nodeId, final PushResult result )
    {
        final ContentId contentId = ContentId.from( nodeId.toString() );
        final PushContentListener.PushResult pushResult =
            result == PushResult.FAILED ? PushContentListener.PushResult.FAILED : PushContentListener.PushResult.PUSHED;
        pushContentListener.contentPushed( contentId, pushResult );
    }


    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private Branch target;

        private boolean includeDependencies = true;

        private boolean includeChildren = true;

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

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public PublishContentCommand build()
        {
            validate();
            return new PublishContentCommand( this );
        }

    }
}