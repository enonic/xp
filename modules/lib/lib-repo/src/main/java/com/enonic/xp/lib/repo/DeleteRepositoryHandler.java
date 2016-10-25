package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

@SuppressWarnings("unused")
public class DeleteRepositoryHandler
    implements ScriptBean
{
    private String repositoryId;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public boolean execute()
    {
        try
        {
            final DeleteRepositoryParams deleteRepositoryParams = DeleteRepositoryParams.from( repositoryId );
            return repositoryServiceSupplier.
                get().
                deleteRepository( deleteRepositoryParams ) != null;
        }
        catch ( RepositoryNotFoundException e )
        {
            return false;
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
