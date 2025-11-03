package com.enonic.xp.repo.impl.repository;

import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;

class Xp8IndexMigrator
{
    final RepositoryService repositoryService;
    final IndexServiceInternal indexServiceInternal;

    public Xp8IndexMigrator( final RepositoryService repositoryService, final IndexServiceInternal indexServiceInternal )
    {
        this.repositoryService = repositoryService;
        this.indexServiceInternal = indexServiceInternal;
    }

    public void migrate()
    {
        for ( Repository repository : repositoryService.list() )
        {
            final Map<String, Object> indexSettings = this.indexServiceInternal.getIndexMapping( repository.getId(), Branch.from( "master" ), IndexType.VERSION );
            if ( indexSettings != null )
            {
                continue;
            }
        }
    }
}
