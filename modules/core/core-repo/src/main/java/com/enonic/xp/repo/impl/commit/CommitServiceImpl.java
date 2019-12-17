package com.enonic.xp.repo.impl.commit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.commit.storage.CommitStorageRequestFactory;
import com.enonic.xp.repo.impl.commit.storage.NodeCommitEntryFactory;
import com.enonic.xp.repo.impl.storage.CommitStorageName;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;

@Component
public class CommitServiceImpl
    implements CommitService
{
    private static final ReturnFields COMMIT_RETURN_FIELDS =
        ReturnFields.from( CommitIndexPath.COMMIT_ID, CommitIndexPath.MESSAGE, CommitIndexPath.TIMESTAMP, CommitIndexPath.COMMITTER );

    private StorageDao storageDao;

    @Override
    public String store( final NodeCommitEntry nodeBranchEntry, final InternalContext context )
    {
        final StoreRequest storeRequest = CommitStorageRequestFactory.create( nodeBranchEntry, context );
        return storageDao.store( storeRequest );
    }

    @Override
    public NodeCommitEntry get( final NodeCommitId nodeCommitId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = createGetByIdRequest( nodeCommitId, context );
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeCommitEntryFactory.create( getResult.getReturnValues() );
    }

    private GetByIdRequest createGetByIdRequest( final NodeCommitId nodeCommitId, final InternalContext context )
    {
        return GetByIdRequest.create().
            id( nodeCommitId.toString() ).
            storageSettings( createCommitStorageSettings( context ) ).
            returnFields( COMMIT_RETURN_FIELDS ).
            build();
    }

    private StorageSource createCommitStorageSettings( final InternalContext context )
    {
        return StorageSource.create().
            storageName( CommitStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.COMMIT ).
            build();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
