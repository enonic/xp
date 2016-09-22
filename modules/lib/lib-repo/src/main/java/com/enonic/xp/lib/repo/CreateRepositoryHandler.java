package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class CreateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private boolean checkExists;

    private boolean checkParentExists;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setCheckExists( final boolean checkExists )
    {
        this.checkExists = checkExists;
    }

    public void setCheckParentExists( final boolean checkParentExists )
    {
        this.checkParentExists = checkParentExists;
    }

    public RepositoryMapper execute()
    {
        final RepositorySettings repositorySettings = RepositorySettings.create().
            indexConfigs( null ).
            validationSettings( null ).
            build();

        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            repositorySettings( repositorySettings ).
            build();

        final Repository repository = repositoryServiceSupplier.
            get().
            create( createRepositoryParams );

        return repository == null ? null : new RepositoryMapper( repository );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
