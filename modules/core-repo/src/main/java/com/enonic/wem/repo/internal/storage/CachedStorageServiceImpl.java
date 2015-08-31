package com.enonic.wem.repo.internal.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.wem.repo.internal.storage.result.GetResult;

@Component
public class CachedStorageServiceImpl
    implements StorageService
{
    private StorageDao storageDao;

    private StorageCache storageCache;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceInternal.class );

    @Override
    public String store( final StoreRequest request, final InternalContext context )
    {
        final String id = this.storageDao.store( request );

        storageCache.put( StoreRequest.from( request ).
            id( id ).
            build() );

        // LOG.info( "Stored in cache: " + id );

        return id;
    }

    @Override
    public boolean delete( final DeleteRequest request, final InternalContext context )
    {
        storageCache.remove( request );

        //LOG.info( "Removed from cache" );

        return this.storageDao.delete( request );
    }

    @Override
    public GetResult getById( final GetByIdRequest request, final InternalContext context )
    {
        //LOG.info( "Fetching: " + request.getId() );

        final GetResult result = storageCache.getById( request );

        if ( result != null )
        {
            //LOG.info( "Fetched in cache" );
            return result;
        }

        return storageDao.getById( request );
    }

    @Override
    public GetResult getByPath( final GetByPathRequest request, final InternalContext context )
    {
        return null;
    }

    @Override
    public GetResult getByParent( final GetByParentRequest request, final InternalContext context )
    {
        return null;
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }

    @Reference
    public void setStorageCache( final StorageCache storageCache )
    {
        this.storageCache = storageCache;
    }
}
