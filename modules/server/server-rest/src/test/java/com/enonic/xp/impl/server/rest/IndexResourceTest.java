package com.enonic.xp.impl.server.rest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class IndexResourceTest
    extends ServerRestTestSupport
{
    private IndexService indexService;

    private RepositoryService repoService;

    private IndexResource resource;

    @Before
    public void setup()
    {
    }

    @Override
    protected IndexResource getResourceInstance()
    {
        indexService = Mockito.mock( IndexService.class );
        repoService = Mockito.mock( RepositoryService.class );

        resource = new IndexResource();
        resource.setIndexService( indexService );
        resource.setRepositoryService( repoService );

        return resource;
    }

    @Test
    public void listRepositories()
    {
        Repositories.Builder builder = Repositories.create();
        for ( int i = 0; i < 5; i++ )
        {
            builder.add( Repository.create().id( RepositoryId.from( "repo-" + i ) ).branches( RepositoryConstants.MASTER_BRANCH ).build() );
        }
        Mockito.when( repoService.list() ).thenReturn( builder.build() );

        final RepositoriesJson result = resource.listRepositories();
        assertEquals( 5, result.repositories.size() );
    }
}
