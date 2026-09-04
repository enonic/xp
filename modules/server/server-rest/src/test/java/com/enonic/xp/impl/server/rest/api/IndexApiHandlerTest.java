package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndexApiHandlerTest
{
    private static final RepositoryId REPO = RepositoryId.from( "com.enonic.cms.default" );

    private IndexService indexService;

    private RepositoryService repositoryService;

    private TaskService taskService;

    private IndexApiHandler handler;

    @BeforeEach
    void setUp()
    {
        indexService = mock( IndexService.class );
        repositoryService = mock( RepositoryService.class );
        taskService = mock( TaskService.class );
        handler = new IndexApiHandler( indexService, repositoryService, taskService );

        when( repositoryService.get( REPO ) ).thenReturn(
            Repository.create().id( REPO ).branches( Branch.from( "draft" ), Branch.from( "master" ) ).build() );
    }

    @Test
    void getReplicas()
    {
        when( indexService.getIndexSettings( REPO, IndexType.SEARCH ) ).thenReturn(
            Map.of( "index.number_of_replicas", "1", "index.auto_expand_replicas", "false" ) );
        when( indexService.getIndexSettings( REPO, IndexType.VERSION ) ).thenReturn(
            Map.of( "index.number_of_replicas", "3", "index.auto_expand_replicas", "0-all" ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:index/" + REPO ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"replicas\":{\"search\":\"1\",\"storage\":\"0-all\"}}", response.getBody() );
    }

    @Test
    void unknownRepository()
    {
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:index/nope" ) ).getStatus() );
        assertEquals( HttpStatus.NOT_FOUND,
                      handler.handle( request( HttpMethod.PUT, "/server:index/nope", "{\"replicas\":{\"search\":\"1\",\"storage\":\"1\"}}" ) )
                          .getStatus() );
    }

    @Test
    void updateReplicas()
    {
        when( indexService.updateIndexSettings( any() ) ).thenReturn( UpdateIndexSettingsResult.create().build() );
        when( indexService.getIndexSettings( eq( REPO ), any() ) ).thenReturn( Map.of( "index.number_of_replicas", "2" ) );

        final WebResponse response =
            handler.handle( request( HttpMethod.PUT, "/server:index/" + REPO, "{\"replicas\":{\"search\":\"2\",\"storage\":\"0-all\"}}" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );

        final ArgumentCaptor<UpdateIndexSettingsParams> captor = ArgumentCaptor.forClass( UpdateIndexSettingsParams.class );
        verify( indexService, times( 2 ) ).updateIndexSettings( captor.capture() );

        final UpdateIndexSettingsParams search = captor.getAllValues().get( 0 );
        assertEquals( IndexType.SEARCH, search.getIndexType() );
        assertEquals( "{\"index\":{\"number_of_replicas\":\"2\",\"auto_expand_replicas\":\"false\"}}", search.getSettings() );
        assertTrue( search.getRepositoryIds().contains( REPO ) );

        final UpdateIndexSettingsParams storage = captor.getAllValues().get( 1 );
        assertEquals( IndexType.VERSION, storage.getIndexType() );
        assertEquals( "{\"index\":{\"auto_expand_replicas\":\"0-all\"}}", storage.getSettings() );
    }

    @Test
    void updateRequiresBothIndices()
    {
        final WebResponse response = handler.handle( request( HttpMethod.PUT, "/server:index/" + REPO, "{\"replicas\":{\"search\":\"2\"}}" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
        assertTrue( String.valueOf( response.getBody() ).contains( "replicas.storage" ) );
        verify( indexService, never() ).updateIndexSettings( any() );
    }

    @Test
    void updateRejectsRawSettings()
    {
        final WebResponse response = handler.handle(
            request( HttpMethod.PUT, "/server:index/" + REPO, "{\"replicas\":{\"search\":\"1\",\"storage\":\"{\\\"refresh_interval\\\":\\\"1s\\\"}\"}}" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
        verify( indexService, never() ).updateIndexSettings( any() );
    }

    @Test
    void reindexDefaultsToAllBranches()
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:index/" + REPO + "/reindex" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        assertEquals( "{\"taskId\":\"t1\"}", response.getBody() );

        final SubmitTaskParams params = submitted();
        assertEquals( "com.enonic.xp.app.system:reindex", params.getDescriptorKey().toString() );
        assertEquals( REPO.toString(), params.getData().getString( "repository" ) );
        assertEquals( List.of( "draft", "master" ), Lists.newArrayList( params.getData().getStrings( "branches" ) ) );
        assertEquals( Boolean.FALSE, params.getData().getBoolean( "initialize" ) );
    }

    @Test
    void reindexNamedBranches()
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );

        handler.handle( request( HttpMethod.POST, "/server:index/" + REPO + "/reindex", "{\"branches\":[\"master\"],\"initialize\":true}" ) );

        final SubmitTaskParams params = submitted();
        assertEquals( List.of( "master" ), Lists.newArrayList( params.getData().getStrings( "branches" ) ) );
        assertEquals( Boolean.TRUE, params.getData().getBoolean( "initialize" ) );
    }

    private SubmitTaskParams submitted()
    {
        final ArgumentCaptor<SubmitTaskParams> captor = ArgumentCaptor.forClass( SubmitTaskParams.class );
        verify( taskService ).submitTask( captor.capture() );
        return captor.getValue();
    }

    @Test
    void policy()
    {
        final Map<String, String> policy = Map.of( "api.server:index.verbs", "get" );
        when( indexService.getIndexSettings( eq( REPO ), any() ) ).thenReturn( Map.of( "index.number_of_replicas", "1" ) );

        assertEquals( HttpStatus.OK, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:index/" + REPO ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:index/" + REPO + "/reindex" ) ) ).getStatus() );
        verify( taskService, never() ).submitTask( any() );
    }
}
