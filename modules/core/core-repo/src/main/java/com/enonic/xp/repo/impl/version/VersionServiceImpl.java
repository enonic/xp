package com.enonic.xp.repo.impl.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repo.impl.version.storage.VersionStorageDocFactory;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private StorageDao storageDao;

    @Override
    public void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( nodeVersionMetadata, context.getRepositoryId() );

        this.storageDao.store( storeRequest );
    }

    @Override
    public void delete( final NodeIds nodeIds, final InternalContext context )
    {
        storageDao.delete( DeleteRequests.create().
            forceRefresh( false ).
            ids( nodeIds.getAsStrings() ).
            settings( createStorageSettings( context ) ).
            build() );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeVersionDocumentId nodeVersionDocumentId, final InternalContext context )
    {
        return doGetById( nodeVersionDocumentId, context );
    }

    private NodeVersionMetadata doGetById( final NodeVersionDocumentId nodeVersionDocumentId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( nodeVersionDocumentId.toString() ).
            returnFields( VERSION_RETURN_FIELDS ).
            storageSettings( createStorageSettings( context ) ).
            routing( nodeVersionDocumentId.getNodeId().toString() ).
            build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeVersionFactory.create( getResult );
    }

    private StorageSource createStorageSettings( final InternalContext context )
    {
        return StorageSource.create().
            storageName( StoreStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.VERSION ).
            build();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
