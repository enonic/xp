package com.enonic.wem.repo.internal.index;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.index.IndexService;
import com.enonic.wem.api.index.IndexType;
import com.enonic.wem.api.index.ReindexParams;
import com.enonic.wem.api.index.ReindexResult;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private final RepositoryInitializer repositoryInitializer;

    private IndexServiceInternal indexServiceInternal;

    public IndexServiceImpl()
    {
        this.repositoryInitializer = new RepositoryInitializer( indexServiceInternal );
    }

    @Override
    public ReindexResult reindex( final ReindexParams params )
    {
        if ( params.isInitialize() )
        {
            repositoryInitializer.initializeRepository( params.getRepositoryId() );
        }

        for ( final IndexType indexType : params.getIndexTypes() )
        {



        }



        return null;
    }

    @Reference
    public IndexServiceInternal getIndexServiceInternal()
    {
        return indexServiceInternal;
    }
}
