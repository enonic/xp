package com.enonic.xp.repo.impl.branch.storage;

import java.util.Collection;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.cache.PathCache;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.GetResults;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreStorageName;

class GetBranchEntriesMethod
    implements BranchEntriesExecutorMethod
{
    private final InternalContext context;

    private final PathCache pathCache;

    private final ReturnFields returnFields;

    private final StorageDao storageDao;

    private GetBranchEntriesMethod( final Builder builder )
    {
        context = builder.context;
        pathCache = builder.pathCache;
        returnFields = builder.returnFields;
        storageDao = builder.storageDao;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void execute( final Collection<NodeId> nodeIds, final NodeBranchEntries.Builder builder )
    {
        final GetByIdsRequest getByIdsRequest = new GetByIdsRequest();

        for ( final NodeId nodeId : nodeIds )
        {
            getByIdsRequest.add( GetByIdRequest.create().
                id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
                storageSettings( StorageSource.create().
                    storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                    storageType( StaticStorageType.BRANCH ).
                    build() ).
                returnFields( returnFields ).
                routing( nodeId.toString() ).
                build() );
        }

        final GetResults getResults = this.storageDao.getByIds( getByIdsRequest );

        for ( final GetResult getResult : getResults )
        {
            if ( !getResult.isEmpty() )
            {
                final NodeBranchEntry nodeBranchEntry = NodeBranchVersionFactory.create( getResult.getReturnValues() );
                builder.add( nodeBranchEntry );
            }
        }
    }

    public static final class Builder
    {
        private InternalContext context;

        private PathCache pathCache;

        private ReturnFields returnFields;

        private StorageDao storageDao;

        private Builder()
        {
        }

        public Builder context( final InternalContext val )
        {
            context = val;
            return this;
        }

        public Builder pathCache( final PathCache val )
        {
            pathCache = val;
            return this;
        }

        public Builder returnFields( final ReturnFields val )
        {
            returnFields = val;
            return this;
        }

        public Builder storageDao( final StorageDao val )
        {
            storageDao = val;
            return this;
        }

        public GetBranchEntriesMethod build()
        {
            return new GetBranchEntriesMethod( this );
        }
    }
}
