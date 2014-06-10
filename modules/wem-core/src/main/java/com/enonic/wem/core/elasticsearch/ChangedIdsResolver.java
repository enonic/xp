package com.enonic.wem.core.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;

public class ChangedIdsResolver
{

    static EntityIds resolve( final BucketAggregation aggregation )
    {
        final Set<EntityId> entityIds = Sets.newLinkedHashSet();

        for ( final Bucket bucket : aggregation.getBuckets() )
        {
            if ( bucket.getDocCount() == 1 )
            {
                final WorkspaceDocumentId workspaceDocumentId = WorkspaceDocumentId.from( bucket.getKey() );
                entityIds.add( workspaceDocumentId.getEntityId() );
            }
            else
            {
                break;
            }
        }

        return EntityIds.from( entityIds );
    }

}
