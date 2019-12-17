package com.enonic.xp.repo.impl.vacuum.blob;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.ValueFilter;

public class IsBlobUsedByVersionCommand
{
    private NodeService nodeService;

    private IndexPath fieldPath;

    private BlobKey blobKey;

    private IsBlobUsedByVersionCommand( final Builder builder )
    {
        nodeService = builder.nodeService;
        fieldPath = builder.fieldPath;
        blobKey = builder.blobKey;
    }

    public boolean execute()
    {
        final NodeVersionQuery query = createQuery();
        final NodeVersionQueryResult versions = nodeService.findVersions( query );
        return versions.getTotalHits() > 0;
    }

    private NodeVersionQuery createQuery()
    {
        final ValueFilter mustHaveBinaryBlobKey = ValueFilter.create().
            fieldName( fieldPath.getPath() ).
            addValue( ValueFactory.newString( blobKey.toString() ) ).
            build();

        return NodeVersionQuery.create().
            size( 0 ).
            addQueryFilter( mustHaveBinaryBlobKey ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeService nodeService;

        private IndexPath fieldPath;

        private BlobKey blobKey;

        private Builder()
        {
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder fieldPath( final IndexPath fieldPath )
        {
            this.fieldPath = fieldPath;
            return this;
        }

        public Builder blobKey( final BlobKey blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }

        public IsBlobUsedByVersionCommand build()
        {
            Preconditions.checkNotNull( nodeService, "nodeService cannot be null" );
            Preconditions.checkNotNull( fieldPath, "fieldPath cannot be null" );
            Preconditions.checkNotNull( blobKey, "blobKey cannot be null" );
            return new IsBlobUsedByVersionCommand( this );
        }
    }
}
