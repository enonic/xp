package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class CreateBranchHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private String branchId;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setBranchId( final String branchId )
    {
        this.branchId = branchId;
    }

    public boolean execute()
    {
        final CreateBranchParams createBranchParams = CreateBranchParams.from( branchId );
        final Branch createdBranch = repositoryServiceSupplier.
            get().
            createBranch( createBranchParams );

        return createdBranch != null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
