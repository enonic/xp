package com.enonic.wem.repo.internal.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.ReturnFields;
import com.enonic.wem.repo.internal.StorageSettings;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetResult;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.version.storage.VersionStorageDocFactory;
import com.enonic.xp.node.NodeVersion;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private StorageDao storageDao;

    @Override
    public void store( final NodeVersionDocument document, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( document, context.getRepositoryId() );

        this.storageDao.store( storeRequest );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionDocumentId nodeVersionDocumentId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( nodeVersionDocumentId.toString() ).
            returnFields( VERSION_RETURN_FIELDS ).
            storageSettings( createStorageSettings( context ) ).
            build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeVersionFactory.create( getResult );
    }

    private StorageSettings createStorageSettings( final InternalContext context )
    {
        return StorageSettings.create().
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
