package com.enonic.xp.repo.impl.version;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.RoutableId;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private static final ReturnFields VERSION_RETURN_FIELDS = ReturnFields.from( VersionIndexPath.entryFields() );

    private final StorageDao storageDao;

    @Activate
    public VersionServiceImpl( @Reference final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }

    @Override
    public void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( nodeVersionMetadata, context.getRepositoryId() );

        this.storageDao.store( storeRequest );
    }

    @Override
    public void delete( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        storageDao.delete( DeleteRequests.create()
                               .ids( List.of( new RoutableId( nodeVersionId.toString() ) ) )
                               .settings( createStorageSettings( context.getRepositoryId() ) )
                               .build() );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( nodeVersionId.toString() )
            .returnFields( VERSION_RETURN_FIELDS )
            .storageSettings( createStorageSettings( context.getRepositoryId() ) )
            .searchPreference( context.getSearchPreference() )
            .build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeVersionFactory.create( getResult.getReturnValues() );
    }

    private StorageSource createStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create().
            storageName( StoreStorageName.from( repositoryId ) ).
            storageType( StaticStorageType.VERSION ).
            build();
    }
}
