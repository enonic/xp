package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.repository.ValidationSettings;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

@SuppressWarnings("unused")
public class CreateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private boolean checkExists = true;

    private boolean checkParentExists = true;

    private boolean checkPermissions = true;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setCheckExists( final Boolean checkExists )
    {
        if ( checkExists != null )
        {
            this.checkExists = checkExists;
        }
    }

    public void setCheckParentExists( final Boolean checkParentExists )
    {
        if ( checkParentExists != null )
        {
            this.checkParentExists = checkParentExists;
        }
    }

    public void setCheckPermissions( final Boolean checkPermissions )
    {
        if ( checkPermissions != null )
        {
            this.checkPermissions = checkPermissions;
        }
    }

    public RepositoryMapper execute()
    {
        final ValidationSettings validationSettings = ValidationSettings.create().
            checkExists( checkExists ).
            checkParentExists( checkParentExists ).
            checkPermissions( checkPermissions ).
            build();
        final RepositorySettings repositorySettings = RepositorySettings.create().
            indexConfigs( null ).
            validationSettings( validationSettings ).
            build();

        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            repositorySettings( repositorySettings ).
            build();

        final Repository repository = repositoryServiceSupplier.
            get().
            createRepository( createRepositoryParams );

        return repository == null ? null : new RepositoryMapper( repository );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
