package com.enonic.xp.repo.impl.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.SearchStorageType;

@Component
public class IndexedDataServiceImpl
    implements IndexedDataService
{
    private StorageDao storageDao;

    @Override
    public ReturnValues get( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context )
    {
        final GetResult result = storageDao.getById( GetByIdRequest.create().
            storageSettings( StorageSettings.create().
                storageType( SearchStorageType.from( context.getBranch() ) ).
                storageName( SearchStorageName.from( context.getRepositoryId() ) ).
                build() ).
            returnFields( returnFields ).
            id( nodeId.toString() ).
            build() );

        return result.getReturnValues();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
