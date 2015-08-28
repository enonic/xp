package com.enonic.wem.repo.internal.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.wem.repo.internal.index.result.GetResult;

@Component
public class CachedStorageDaoImpl
    implements StorageDao
{
    private StorageDaoInternal storageDaoInternal;

    private StorageCache storageCache;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceInternal.class );

    @Override
    public String store( final StoreRequest request )
    {
        final String id = this.storageDaoInternal.store( request );

        storageCache.put( StoreRequest.from( request ).
            id( id ).
            build() );

        return id;
    }

    @Override
    public boolean delete( final DeleteRequest request )
    {
        storageCache.remove( request );

        LOG.info( "Removed from cache" );

        return this.storageDaoInternal.delete( request );
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        final GetResult result = storageCache.getById( request );

        if ( result != null )
        {
            LOG.info( "Fetched in cache" );
            return result;
        }

        return storageDaoInternal.getById( request );
    }

    @Override
    public GetResult getByPath( final GetByPathRequest request )
    {
        return null;
    }

    @Override
    public GetResult getByParent( final GetByParentRequest request )
    {
        return null;
    }

    @Reference
    public void setStorageDaoInternal( final StorageDaoInternal storageDaoInternal )
    {
        this.storageDaoInternal = storageDaoInternal;
    }

    @Reference
    public void setStorageCache( final StorageCache storageCache )
    {
        this.storageCache = storageCache;
    }
}
