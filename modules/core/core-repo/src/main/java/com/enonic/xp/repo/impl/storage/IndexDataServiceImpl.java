package com.enonic.xp.repo.impl.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.SearchStorageType;

@Component
public class IndexDataServiceImpl
    implements IndexDataService
{
    private StorageDao storageDao;

    @Override
    public ReturnValues get( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context )
    {
        final GetResult result = storageDao.getById( createGetByIdRequest( nodeId, returnFields, context ) );

        return result.getReturnValues();
    }

    private GetByIdRequest createGetByIdRequest( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context )
    {
        return GetByIdRequest.create().
            storageSettings( StorageSettings.create().
                storageType( SearchStorageType.from( context.getBranch() ) ).
                storageName( SearchStorageName.from( context.getRepositoryId() ) ).
                build() ).
            returnFields( returnFields ).
            id( nodeId.toString() ).
            build();
    }

    @Override
    public ReturnValues get( final NodeIds nodeIds, final ReturnFields returnFields, final InternalContext context )
    {
        final GetByIdsRequest getByIdsRequest = new GetByIdsRequest();

        for ( final NodeId nodeId : nodeIds )
        {
            getByIdsRequest.add( createGetByIdRequest( nodeId, returnFields, context ) );
        }

        final GetResults result = storageDao.getByIds( getByIdsRequest );

        final ReturnValues.Builder allResultValues = ReturnValues.create();

        for ( GetResult getResult : result )
        {
            final ReturnValues returnValues = getResult.getReturnValues();

            for ( final String key : returnValues.getReturnValues().keySet() )
            {
                allResultValues.add( key, returnValues.get( key ).getValues() );
            }
        }

        return allResultValues.build();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
