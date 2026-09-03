package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebappApiHandlerTest
{
    @Test
    void list()
    {
        final ApplicationService applicationService = mock( ApplicationService.class );
        final ResourceService resourceService = mock( ResourceService.class );

        final Application withWebapp = mock( Application.class );
        when( withWebapp.getKey() ).thenReturn( ApplicationKey.from( "com.enonic.app.site" ) );
        final Application withoutWebapp = mock( Application.class );
        when( withoutWebapp.getKey() ).thenReturn( ApplicationKey.from( "com.enonic.app.lib" ) );
        when( applicationService.getInstalledApplications() ).thenReturn( Applications.from( withWebapp, withoutWebapp ) );

        final Resource missing = mock( Resource.class );
        when( missing.exists() ).thenReturn( false );
        when( resourceService.getResource( any() ) ).thenReturn( missing );
        final ResourceKey webappKey = ResourceKey.from( ApplicationKey.from( "com.enonic.app.site" ), "/webapp/webapp.js" );
        final Resource present = mock( Resource.class );
        when( present.exists() ).thenReturn( true );
        when( present.getKey() ).thenReturn( webappKey );
        when( resourceService.getResource( webappKey ) ).thenReturn( present );

        final WebResponse response = new WebappApiHandler( applicationService, resourceService ).handle( request( HttpMethod.GET, "/server:webapp" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"webapps\":[{\"name\":\"com.enonic.app.site\"}]}", response.getBody() );
    }
}
