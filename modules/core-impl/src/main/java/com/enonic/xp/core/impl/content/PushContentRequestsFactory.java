package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePublishRequest;
import com.enonic.xp.node.NodePublishRequests;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.ResolveSyncWorkResults;

class PushContentRequestsFactory
{

    private final ResolveSyncWorkResults syncWorkResults;

    private final boolean forceInitialReasonInclusion;

    private PushContentRequestsFactory( final Builder builder )
    {
        this.forceInitialReasonInclusion = builder.forceInitialReasonInclusion;
        this.syncWorkResults = builder.syncWorkResults;
    }

    public PushContentRequests createRequests()
    {
        final PushContentRequests.Builder builder = PushContentRequests.create();

        for ( final ResolveSyncWorkResult syncWorkResult : this.syncWorkResults )
        {
            doCreate( builder, syncWorkResult.getNodePublishRequests(), syncWorkResult.getInitialReasonNodeId() );
            doCreateDeleted( builder, syncWorkResult.getNodeDeleteRequests(), syncWorkResult.getInitialReasonNodeId() );
            if ( this.forceInitialReasonInclusion )
            {
                ensureInitialNodeIncluded( builder, syncWorkResult.getInitialReasonNodeId() );
            }
        }

        return builder.build();
    }

    private void ensureInitialNodeIncluded( final PushContentRequests.Builder builder, final NodeId initialReasonNodeId )
    {
        if ( !builder.getMapWithInitialReasonContentIds().containsKey( initialReasonNodeId ) )
        {
            builder.addRequested( ContentId.from( initialReasonNodeId.toString() ), ContentId.from( initialReasonNodeId.toString() ) );
        }
    }

    private void doCreate( final PushContentRequests.Builder builder, final NodePublishRequests nodePublishRequests,
                                  final NodeId initialReasonNodeId )
    {
        for ( final NodePublishRequest parentOf : nodePublishRequests.getPublishAsParentFor() )
        {
            builder.addParentOf( ContentId.from( parentOf.getNodeId().toString() ),
                                 ContentId.from( parentOf.getReason().getContextualNodeId().toString() ),
                                 ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest referredTo : nodePublishRequests.getPublishAsReferredTo() )
        {
            builder.addReferredTo( ContentId.from( referredTo.getNodeId().toString() ),
                                   ContentId.from( referredTo.getReason().getContextualNodeId().toString() ),
                                   ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest requested : nodePublishRequests.getPublishAsRequested() )
        {
            builder.addRequested( ContentId.from( requested.getNodeId().toString() ), ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest childOf : nodePublishRequests.getPublishAsChildOf() )
        {
            builder.addChildOf( ContentId.from( childOf.getNodeId().toString() ),
                                ContentId.from( childOf.getReason().getContextualNodeId().toString() ),
                                ContentId.from( initialReasonNodeId.toString() ) );
        }
    }

    private void doCreateDeleted( final PushContentRequests.Builder builder, final NodePublishRequests nodePublishRequests,
                                         final NodeId initialReasonNodeId )
    {
        for ( final NodePublishRequest parentOf : nodePublishRequests.getPublishAsParentFor() )
        {
            builder.addDeleteBecauseParentOf( ContentId.from( parentOf.getNodeId().toString() ),
                                              ContentId.from( parentOf.getReason().getContextualNodeId().toString() ),
                                              ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest referredTo : nodePublishRequests.getPublishAsReferredTo() )
        {
            builder.addDeleteBecauseReferredTo( ContentId.from( referredTo.getNodeId().toString() ),
                                                ContentId.from( referredTo.getReason().getContextualNodeId().toString() ),
                                                ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest requested : nodePublishRequests.getPublishAsRequested() )
        {
            builder.addDeleteRequested( ContentId.from( requested.getNodeId().toString() ),
                                        ContentId.from( initialReasonNodeId.toString() ) );
        }

        for ( final NodePublishRequest childOf : nodePublishRequests.getPublishAsChildOf() )
        {
            builder.addDeleteBecauseChildOf( ContentId.from( childOf.getNodeId().toString() ),
                                             ContentId.from( childOf.getReason().getContextualNodeId().toString() ),
                                             ContentId.from( initialReasonNodeId.toString() ) );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ResolveSyncWorkResults syncWorkResults;

        private boolean forceInitialReasonInclusion;

        public Builder syncWorkResults( final ResolveSyncWorkResults syncWorkResults )
        {
            this.syncWorkResults = syncWorkResults;
            return this;
        }

        public Builder forceInitialReasonInclusion( final boolean forceInitialReasonInclusion )
        {
            this.forceInitialReasonInclusion = forceInitialReasonInclusion;
            return this;
        }

        public PushContentRequestsFactory build()
        {
            return new PushContentRequestsFactory( this );
        }
    }

}
