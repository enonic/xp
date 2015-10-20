package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
// https://github.com/enonic/cms/blob/master/cms-ee/cms-ee-core/src/main/java/com/enonic/cms/ee/cluster/ClusterEventService.java
public final class ClusterEventSender
    implements EventListener
{
    @Override
    public void onEvent( final Event event )
    {

        // In ElasticSearch module we need to publish the following
        /*
        final InternalNode internalNode = (InternalNode) this.node;
        this.transportService = internalNode.injector().getInstance( TransportService.class );
        this.clusterService = internalNode.injector().getInstance( ClusterService.class );
         */
    }
}
