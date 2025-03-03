package com.enonic.xp.impl.server.rest;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;

public class JsonIndexResourceTest
    extends JaxRsResourceTestSupport
{
    private IndexService indexService;

    private RepositoryService repositoryService;

    @Test
    public void reindex()
        throws Exception
    {
        final ReindexResult reindexResult = ReindexResult.create().
            repositoryId( RepositoryId.from( "repo-id" ) ).
            branches( Branches.from( Branch.from( "branch1" ) ) ).
            duration( Duration.ofMillis( 41416 ) ).
            startTime( Instant.ofEpochMilli( 1438866915875L ) ).
            endTime( Instant.ofEpochMilli( 1438866957291L ) ).
            build();

        Mockito.when( this.indexService.reindex( isA( ReindexParams.class ) ) ).thenReturn( reindexResult );

        final String result = request().path( "repo/index/reindex" )
            .entity( readFromFile( "reindex_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "reindex.json", result );
    }

    @Test
    public void updateSettings()
        throws Exception
    {
        final UpdateIndexSettingsResult indexSettingsResult = UpdateIndexSettingsResult.create().
            addUpdatedIndex( "index1" ).
            addUpdatedIndex( "index2" ).
            build();

        Mockito.when( this.indexService.updateIndexSettings( isA( UpdateIndexSettingsParams.class ) ) ).
            thenReturn( indexSettingsResult );

        Mockito.when( this.repositoryService.list() ).thenReturn(
            Repositories.from( Repository.create().id( RepositoryId.from( "my-repo" ) ).branches( Branch.from( "master" ) ).build() ) );

        final String result = request().path( "repo/index/updateSettings" )
            .entity( readFromFile( "update_index_settings_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_index_settings.json", result );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.indexService = mock( IndexService.class );
        this.repositoryService = mock( RepositoryService.class );

        final IndexResource resource = new IndexResource();
        resource.setIndexService( indexService );
        resource.setRepositoryService( repositoryService );
        return resource;
    }
}
