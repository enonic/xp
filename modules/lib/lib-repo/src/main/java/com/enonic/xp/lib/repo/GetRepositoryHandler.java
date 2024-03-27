package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GetRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = RepositoryId.from( repositoryId );
    }

    public RepositoryMapper execute()
    {
        final Repository repository = repositoryServiceSupplier.
            get().
            get( repositoryId );

        return repository == null ? null : new RepositoryMapper( repository );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
