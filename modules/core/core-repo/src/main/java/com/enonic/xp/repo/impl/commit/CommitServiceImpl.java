package com.enonic.xp.repo.impl.commit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.commit.storage.CommitStorageRequestFactory;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;

@Component
public class CommitServiceImpl
    implements CommitService
{
    private StorageDao storageDao;

    @Override
    public String store( final NodeCommitEntry nodeBranchEntry, final InternalContext context )
    {
        final StoreRequest storeRequest = CommitStorageRequestFactory.create( nodeBranchEntry, context );
        return storageDao.store( storeRequest );
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
