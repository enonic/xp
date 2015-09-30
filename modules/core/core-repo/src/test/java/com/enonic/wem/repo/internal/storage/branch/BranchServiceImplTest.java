package com.enonic.wem.repo.internal.storage.branch;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.storage.BranchServiceImpl;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class BranchServiceImplTest
{

    private BranchServiceImpl branchService;

    private StorageDao storageDao;

    private InternalContext context;

    @Before
    public void setup()
        throws Exception
    {
        this.storageDao = Mockito.mock( StorageDao.class );

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );

        context = InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            authInfo( AuthenticationInfo.unAuthenticated() ).
            repositoryId( RepositoryId.from( "myRepo" ) ).
            build();
    }


}