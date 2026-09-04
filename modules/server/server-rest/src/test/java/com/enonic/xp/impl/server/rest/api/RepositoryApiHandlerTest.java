package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryApiHandlerTest
{
    private RepositoryService repositoryService;

    private RepositoryApiHandler handler;

    @BeforeEach
    void setUp()
    {
        repositoryService = mock( RepositoryService.class );
        handler = new RepositoryApiHandler( repositoryService );
    }

    @Test
    void list()
    {
        when( repositoryService.list() ).thenReturn(
            Repositories.from( Repository.create().id( RepositoryId.from( "a" ) ).branches( Branch.from( "master" ) ).build() ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:repo" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"repositories\":[{\"branches\":[\"master\"],\"id\":\"a\"}]}", response.getBody() );
    }

    @Test
    void get()
    {
        when( repositoryService.get( RepositoryId.from( "a" ) ) ).thenReturn(
            Repository.create().id( RepositoryId.from( "a" ) ).branches( Branch.from( "master" ) ).build() );

        assertEquals( "{\"branches\":[\"master\"],\"id\":\"a\"}", handler.handle( request( HttpMethod.GET, "/server:repo/a" ) ).getBody() );
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:repo/b" ) ).getStatus() );
    }
}
