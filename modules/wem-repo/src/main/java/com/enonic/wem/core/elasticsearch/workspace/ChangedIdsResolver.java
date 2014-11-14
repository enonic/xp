package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeIds;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

class ChangedIdsResolver
{

    static NodeIds resolve( final BucketAggregation aggregation )
    {
        final Set<NodeId> nodeIds = Sets.newLinkedHashSet();

        for ( final Bucket bucket : aggregation.getBuckets() )
        {
            if ( bucket.getDocCount() == 1 )
            {
                final WorkspaceDocumentId workspaceDocumentId = WorkspaceDocumentId.from( bucket.getKey() );
                nodeIds.add( workspaceDocumentId.getNodeId() );
            }
            else
            {
                break;
            }
        }

        return NodeIds.from( nodeIds );
    }

}
