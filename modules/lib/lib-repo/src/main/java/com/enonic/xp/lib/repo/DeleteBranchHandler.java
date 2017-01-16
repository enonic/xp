package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.lib.repo.mapper.BranchMapper;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class DeleteBranchHandler
    implements ScriptBean
{
    private String branchId;

    private String repoId;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepoId( final String repoId )
    {
        this.repoId = repoId;
    }

    public void setBranchId( final String branchId )
    {
        this.branchId = branchId;
    }

    public BranchMapper execute()
    {
        return createContext().callWith( this::doDeleteBranch );
    }

    private BranchMapper doDeleteBranch()
    {
        final DeleteBranchParams deleteBranchParams = DeleteBranchParams.from( branchId );
        final Branch deletedBranch = repositoryServiceSupplier.
            get().
            deleteBranch( deleteBranchParams );

        return deletedBranch == null ? null : new BranchMapper( deletedBranch );
    }


    private Context createContext()
    {
        final ContextBuilder builder = ContextBuilder.from( ContextAccessor.current() );

        if ( this.repoId != null )
        {
            builder.repositoryId( repoId );
        }

        return builder.build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
