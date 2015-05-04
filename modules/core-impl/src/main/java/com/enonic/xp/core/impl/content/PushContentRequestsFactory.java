package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.node.NodePublishRequest;
import com.enonic.xp.node.NodePublishRequests;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.ResolveSyncWorkResults;

class PushContentRequestsFactory
{
    public static PushContentRequests create( final ResolveSyncWorkResults syncWorkResults )
    {
        final PushContentRequests.Builder builder = PushContentRequests.create();

        for ( final ResolveSyncWorkResult syncWorkResult : syncWorkResults )
        {
            doCreate( builder, syncWorkResult.getNodePublishRequests() );
        }

        return builder.build();
    }

    private static void doCreate( final PushContentRequests.Builder builder, final NodePublishRequests nodePublishRequests )
    {
        for ( final NodePublishRequest parentOf : nodePublishRequests.getPublishAsParentFor() )
        {
            builder.addParentOf( ContentId.from( parentOf.getNodeId().toString() ),
                                 ContentId.from( parentOf.getReason().getContextualNodeId().toString() ) );
        }

        for ( final NodePublishRequest referredTo : nodePublishRequests.getPublishAsReferredTo() )
        {
            builder.addReferredTo( ContentId.from( referredTo.getNodeId().toString() ),
                                   ContentId.from( referredTo.getReason().getContextualNodeId().toString() ) );
        }

        for ( final NodePublishRequest requested : nodePublishRequests.getPublishAsRequested() )
        {
            builder.addRequested( ContentId.from( requested.getNodeId().toString() ) );
        }

        for ( final NodePublishRequest childOf : nodePublishRequests.getPublishAsChildOf() )
        {
            builder.addChildOf( ContentId.from( childOf.getNodeId().toString() ),
                                ContentId.from( childOf.getReason().getContextualNodeId().toString() ) );
        }
    }
}
