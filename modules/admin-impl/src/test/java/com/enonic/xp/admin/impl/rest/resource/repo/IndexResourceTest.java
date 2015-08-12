package com.enonic.xp.admin.impl.rest.resource.repo;

import java.time.Duration;
import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.RepositoryId;

import static org.mockito.Matchers.isA;

public class IndexResourceTest
    extends AbstractResourceTest
{
    private IndexService indexService;

    @Test
    public void reindex()
        throws Exception
    {
        final ReindexResult reindexResult = ReindexResult.create().
            repositoryId( RepositoryId.from( "repoId" ) ).
            branches( Branches.from( Branch.from( "branch1" ) ) ).
            duration( Duration.ofMillis( 41416 ) ).
            startTime( Instant.ofEpochMilli( 1438866915875L ) ).
            endTime( Instant.ofEpochMilli( 1438866957291L ) ).
            build();

        Mockito.when( this.indexService.reindex( isA( ReindexParams.class ) ) ).thenReturn( reindexResult );

        final String result = request().path( "repo/reindex" ).
            entity( readFromFile( "reindex_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "reindex.json", result );
    }


    @Override
    protected Object getResourceInstance()
    {
        this.indexService = Mockito.mock( IndexService.class );

        final IndexResource resource = new IndexResource();
        resource.setIndexService( indexService );
        return resource;
    }
}
