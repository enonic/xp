package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class CreateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void execute()
    {
        final RepositorySettings repositorySettings = RepositorySettings.create().
            repositoryId( repositoryId ).
            indexConfigs( null ).
            validationSettings( null ).
            build();
        repositoryServiceSupplier.
            get().
            create( repositorySettings );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
