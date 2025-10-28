package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.impl.server.rest.model.WebappJson;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebappResourceTest
{
    private WebappResource resource;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ResourceService resourceService;

    @BeforeEach
    void setUp()
    {
        resource = new WebappResource( applicationService, resourceService );
    }

    @Test
    void testList()
    {
        final List<ApplicationKey> keys =
            List.of( ApplicationKey.from( "app1" ), ApplicationKey.from( "app2" ), ApplicationKey.from( "app3" ) );

        final List<Application> applications = keys.stream().map( key -> {
            final Application app = mock( Application.class );
            when( app.getKey() ).thenReturn( key );

            return app;
        } ).collect( Collectors.toList() );

        final Applications from = Applications.from( applications );
        when( applicationService.getInstalledApplications() ).thenReturn( from );

        final Resource resource1 = mock( Resource.class );
        when( resource1.getKey() ).thenReturn( ResourceKey.from( keys.get( 0 ), "/webapp/webapp.js" ) );
        when( resource1.exists() ).thenReturn( true );

        final Resource resource2 = mock( Resource.class );
        when( resource2.getKey() ).thenReturn( ResourceKey.from( keys.get( 1 ), "/webapp/webapp.js" ) );
        when( resource2.exists() ).thenReturn( false );

        final Resource resource3 = mock( Resource.class );
        when( resource3.getKey() ).thenReturn( ResourceKey.from( keys.get( 2 ), "/webapp/webapp.js" ) );
        when( resource3.exists() ).thenReturn( true );

        final List<Resource> resources = List.of( resource1, resource2, resource3 );

        when( resourceService.getResource( resources.get( 0 ).getKey() ) ).thenReturn( resources.get( 0 ) );
        when( resourceService.getResource( resources.get( 1 ).getKey() ) ).thenReturn( resources.get( 1 ) );
        when( resourceService.getResource( resources.get( 2 ).getKey() ) ).thenReturn( resources.get( 2 ) );

        assertThat( resource.list() ).usingRecursiveComparison()
            .isEqualTo( List.of( WebappJson.from( keys.get( 0 ) ), WebappJson.from( keys.get( 2 ) ) ) );
    }

    @Test
    void testEmptyList()
    {
        when( applicationService.getInstalledApplications() ).thenReturn( Applications.empty() );
        assertThat( resource.list() ).isEmpty();
    }

}
